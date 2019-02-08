package com.joaocarlos.cursomc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.joaocarlos.cursomc.domain.Cliente;
import com.joaocarlos.cursomc.repositories.ClienteRepository;
import com.joaocarlos.cursomc.security.UserSS;

@Service
public class UserDetailServiceimpl implements UserDetailsService {

	@Autowired
	private ClienteRepository clienteRespository;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Cliente cliente = clienteRespository.findByEmail(email);
		if(cliente == null) {
			throw new UsernameNotFoundException(email);
		}
		
		return new UserSS(cliente.getId(), cliente.getEmail(), cliente.getSenha(), cliente.getPerfis());
	}

}
