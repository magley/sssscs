package com.ib.pki;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class SubjectData {
	@Size(min=0, max=30, message="Must contain between 0 and 30 characters")
	@Pattern(regexp = "^[A-Za-z]*$", message="Must be letters.")
	private String name = "";
	@Size(min=0, max=30, message="Must contain between 0 and 30 characters")
	@Pattern(regexp = "^[A-Za-z]*$", message="Must be letters.")
	private String surname = "";
	@Email
	private String email = "";
	@Size(min=0, max=20, message="Must contain between 0 and 20 characters")
	@Pattern(regexp = "^[A-Za-z]*$", message="Must be letters.")
	private String organization = "";
	@Size(min=0, max=30, message="Must contain between 0 and 30 characters")
	@Pattern(regexp = "^[A-Za-z]*$", message="Must be letters.")
	private String commonName = "";

	// TODO: Maybe just create a SubjectDataDTO ?

	@JsonIgnore
	public X500Name getX500Name() {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, commonName);
		builder.addRDN(BCStyle.NAME, name);
		builder.addRDN(BCStyle.SURNAME, surname);
		builder.addRDN(BCStyle.E, email);
		builder.addRDN(BCStyle.O, organization);
		return builder.build();
	}
}
