package com.ib.pki.data;

import java.security.PrivateKey;

import org.bouncycastle.asn1.x500.X500Name;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssuerData {
	private PrivateKey privateKey;
	private X500Name x500name;
}
