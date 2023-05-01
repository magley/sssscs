package com.ib.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // Must not be "user".
public class User implements UserDetails {
	private static final long serialVersionUID = 6494659258011938199L;

	public enum Role {
		REGULAR, ADMIN
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true, length = 100)
	private String email;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false, length = 100)
	private String name;
	@Column(nullable = false, length = 100)
	private String surname;
	@Column(nullable = false, length = 18)
	private String phoneNumber;
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Role role = Role.REGULAR;
	@Column(nullable = false)
	private Boolean verified = false;
	/**
	 * How many times has the user successfully logged in since the last time
	 * we asked him to perform 2FA.
	 * 
	 * When this value reaches "maximum", the user has to verify his identity.
	 * Maximum value is defined in <i>UserController</i>.
	 * Once the user verifies it's him, this value is reset to 0 again.
	 */
	@Column(nullable = false)
	private Integer loginCounter = 0;
	@Column(nullable = false)
	private Boolean blocked = false;
	@Column(nullable = true)
	private LocalDateTime blockEndDate;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + this.getRole().toString()));
	}

	@Override
	public String getUsername() {
		return this.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
