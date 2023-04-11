package com.ib.pki.data;

import java.security.PublicKey;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubjectData {
	private PublicKey publicKey;
	private X500Name x500name;
	private String certSerialNumber;
	private Date startDate;
	private Date endDate;
}
