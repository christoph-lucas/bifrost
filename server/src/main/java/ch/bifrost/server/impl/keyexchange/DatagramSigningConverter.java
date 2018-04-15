package ch.bifrost.server.impl.keyexchange;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateEncodingException;

import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;

import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERBitString;

import com.google.common.base.Optional;

import ch.bifrost.core.api.datagram.CounterpartAddress;
import ch.bifrost.core.api.datagram.DatagramEndpoint;
import ch.bifrost.core.api.datagram.DatagramMessage;
import ch.bifrost.core.impl.datagram.InvalidDatagramException;

public class DatagramSigningConverter implements DatagramEndpoint {

	private DatagramEndpoint endpoint;
	private Signature signature;
	private ASN1Encodable certChain;
	
	@Override
	public void close() throws IOException {
		endpoint.close();
	}

	@Override
	public void send(DatagramMessage packet)
			throws IOException {
		try {
			ASN1Encodable asn1payload = new DERBitString(packet.getPayload(), 0);
			signature.update(packet.getPayload());
			byte[] sign = signature.sign();
			ASN1Encodable asn1sign = new DERBitString(sign, 0);
			// Each message contains: the payload, the signature, and the certificate chain; the latter one encoded as bit string
			ASN1Encodable certString = new DERBitString(certChain.toASN1Primitive().getEncoded(), 0);
			ASN1Encodable[] asn1array = {asn1payload, asn1sign, certString};
			ASN1Encodable asn1message = new DERSequence(asn1array);
			DatagramMessage signedPacket = new DatagramMessage(packet.getCounterpartAddress(), asn1message.toASN1Primitive().getEncoded());
			endpoint.send(signedPacket);
		} catch (SignatureException e) {
			throw new IOException();
		}
	}

	@Override
	public DatagramMessage receive() throws IOException, InterruptedException, InvalidDatagramException {
		return endpoint.receive();
	}

	@Override
	public Optional<DatagramMessage> receive(long timeout, TimeUnit unit)
			throws IOException, InterruptedException, InvalidDatagramException {
		return endpoint.receive(timeout, unit);
	}

	@Override
	public DatagramEndpoint counterpartAddress(CounterpartAddress counterpartAddress) {
		endpoint.counterpartAddress(counterpartAddress);
		return this;
	}

	public DatagramSigningConverter(DatagramEndpoint insecureEndpoint, Certificate[] chain, PrivateKey sk)
			throws CertificateEncodingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
		endpoint = insecureEndpoint;

		// Parse the certificate chain as X.509 and create an ASN.1 sequence from it
		// For the certificate chain to be accepted by the receiver (the current Java implementation), we have to
		// send them in the inverse order. As this is consistent with what TLS does, and the order does not otherwise
		// matter to us, I have decided to just build the packet in this way.
		ASN1Encodable[] certs = new ASN1Encodable[chain.length];
		for (int i = 0; i < chain.length; i++) {
			JcaX509CertificateHolder holder = new JcaX509CertificateHolder((X509Certificate) chain[chain.length - i - 1]);
			certs[i] = holder.toASN1Structure();
		}
		certChain = new DERSequence(certs);
		
		// Initialize the signature object that we can use to authenticate multiple packets
		signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(sk);
	}
}
