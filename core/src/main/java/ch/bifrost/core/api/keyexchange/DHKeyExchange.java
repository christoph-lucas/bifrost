package ch.bifrost.core.api.keyexchange;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.agreement.DHBasicAgreement;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;

public class DHKeyExchange {

	public static final DHParameters DH_PARAMETERS = DHStandardGroups.rfc3526_2048;

	private DHBasicAgreement agreementInstance;
	private DHPublicKeyParameters publicKeyParams;

	public DHKeyExchange () {
		this.agreementInstance = new DHBasicAgreement();

		DHKeyPairGenerator generator = new DHKeyPairGenerator();
		DHKeyGenerationParameters param = new DHKeyGenerationParameters(new SecureRandom(), DH_PARAMETERS);
		generator.init(param);

		AsymmetricCipherKeyPair keyPair = generator.generateKeyPair();
		DHPrivateKeyParameters privateKeyParams = (DHPrivateKeyParameters) keyPair.getPrivate();
		publicKeyParams = (DHPublicKeyParameters) keyPair.getPublic();

		this.agreementInstance.init(privateKeyParams);
	}

	public BigInteger getPublicKey () {
		return this.publicKeyParams.getY();
	}

	public BigInteger getSharedKey (BigInteger receivedPublicKey) {
		DHPublicKeyParameters receivedPublicKeyWithParams = new DHPublicKeyParameters(receivedPublicKey, DH_PARAMETERS);
		return this.agreementInstance.calculateAgreement(receivedPublicKeyWithParams);
	}

}
