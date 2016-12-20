package ch.bifrost.core.api.keyexchange;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DHKeyExchangeRequestMessage {

	private BigInteger clientPublicKey;

	public static DHKeyExchangeRequestMessage from (byte[] publicKeyBytes) {
		BigInteger publicKey = new BigInteger(publicKeyBytes);
		return new DHKeyExchangeRequestMessage(publicKey);
	}

	public byte[] toByteArray () {
		return this.clientPublicKey.toByteArray();
	}

}
