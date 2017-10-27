package ch.bifrost.client.impl.keyexchange;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;

import java.security.cert.CertPath;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.CertPathParameters;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.PKIXCertPathValidatorResult;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1BitString;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.impl.datagram.InvalidDatagramException;

public class DatagramVerifyingConverter implements DatagramEndpoint {

	private DatagramEndpoint endpoint;
	private CertPathParameters pkiParms;
	private CertPathValidator validator;

	@Override
	public void close() throws IOException {
		endpoint.close();
	}

	@Override
	public void send(DatagramMessage packet) throws IOException {
		endpoint.send(packet);
	}

	private DatagramMessage internalReceive(DatagramMessage packet) throws IOException {
		CertPath certPath;
		PKIXCertPathValidatorResult result;
		Signature signature;

		// Parse the ASN1 packet
		ASN1Sequence asn1seq = ASN1Sequence.getInstance(packet.getPayload());
		ASN1BitString message = (ASN1BitString) asn1seq.getObjectAt(0);
		ASN1BitString sign = (ASN1BitString) asn1seq.getObjectAt(1);
		ASN1BitString chain = (ASN1BitString) asn1seq.getObjectAt(2);
		
		// Parse the certification path from the message.
		try {
			CertificateFactory factory = CertificateFactory.getInstance("X.509");
			certPath = factory.generateCertPath(new ByteArrayInputStream(chain.getBytes()));
		} catch (CertificateException e) {
			throw new IOException(e.getMessage());
		}

		// Validate the certificate
		try {
			result = (PKIXCertPathValidatorResult) validator.validate(certPath, pkiParms);
			if (result.getTrustAnchor() == null) { // This means the verification has failed.
				throw new IOException("Verification of certificate chain failed.");
			}
		} catch (CertPathValidatorException | InvalidAlgorithmParameterException e) {
			throw new IOException(e.getMessage());
		}

		// Initialize the signature object with the public key we just verified
		try {
			signature = Signature.getInstance("SHA256withRSA");
			signature.initVerify(result.getPublicKey());
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new IOException(e.getMessage());
		}
		
		// Verify signature
		try {
			signature.update(message.getBytes());
			if (signature.verify(sign.getBytes())) {
				return new DatagramMessage(packet.getCounterpartAddress(), message.getBytes());
			} else {
				throw new IOException("Verification of signature failed.");
			}
		} catch (SignatureException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	@Override
	public DatagramMessage receive() throws IOException, InterruptedException, InvalidDatagramException {
		DatagramMessage packet = endpoint.receive();
		return internalReceive(packet);
	}

	@Override
	public Optional<DatagramMessage> receive(long timeout, TimeUnit unit)
			throws IOException, InterruptedException, InvalidDatagramException {
		Optional<DatagramMessage> packet = endpoint.receive(timeout, unit);
		if (packet.isPresent()) {
			return Optional.of(internalReceive(packet.get()));
		} else {
			return Optional.absent();
		}
	}

	@Override
	public DatagramEndpoint counterpartAddress(CounterpartAddress counterpartAddress) {
		endpoint.counterpartAddress(counterpartAddress);
		return this;
	}

	public DatagramVerifyingConverter(DatagramEndpoint insecureEndpoint, KeyStore trustedCerts) throws KeyStoreException, InvalidAlgorithmParameterException, NoSuchAlgorithmException {
		endpoint = insecureEndpoint;
		PKIXParameters parms = new PKIXParameters(trustedCerts);

		//TODO: This will have to be changed for any real-world deployment, otherwise we keep ignoring CRLs
		parms.setRevocationEnabled(false);
		System.out.println("Certificate revocation check is disabled explicitly!");

		pkiParms = parms;
		validator = CertPathValidator.getInstance("PKIX");
	}
}
