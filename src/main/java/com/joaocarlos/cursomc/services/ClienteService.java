package com.joaocarlos.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joaocarlos.cursomc.domain.Cidade;
import com.joaocarlos.cursomc.domain.Cliente;
import com.joaocarlos.cursomc.domain.Endereco;
import com.joaocarlos.cursomc.domain.enums.TipoCliente;
import com.joaocarlos.cursomc.dto.ClienteDTO;
import com.joaocarlos.cursomc.dto.ClienteNewDTO;
import com.joaocarlos.cursomc.repositories.ClienteRepository;
import com.joaocarlos.cursomc.repositories.EnderecoRepository;
import com.joaocarlos.cursomc.services.exceptions.DataIntegrityException;
import com.joaocarlos.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private BCryptPasswordEncoder password;

	@Autowired
	private ClienteRepository repository;

	@Autowired
	private EnderecoRepository enderecoRepository;

	public Cliente find(Integer id) {
		Optional<Cliente> cat = repository.findById(id);

		return cat.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repository.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateCliente(newObj, obj);
		return repository.save(newObj);
	}

	public void delete(Integer id) {
		find(id);
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir um cliente que possua pedidos realizados.");
		}
	}

	public List<Cliente> findAll() {
		return repository.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repository.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objNewDto) {
		Cliente cliente = new Cliente(null, objNewDto.getNome(), objNewDto.getEmail(), objNewDto.getCpfOuCnpj(),
				TipoCliente.toEnum(objNewDto.getTipo()), password.encode(objNewDto.getSenha()));
		Cidade cidade = new Cidade(objNewDto.getCidadeId(), null, null);
		Endereco endereco = new Endereco(null, objNewDto.getLogradouro(), objNewDto.getNumero(),
				objNewDto.getComplemento(), objNewDto.getBairro(), objNewDto.getCep(), cliente, cidade);
		cliente.getEnderecos().add(endereco);
		cliente.getTelefones().add(objNewDto.getTelefone1());
		if (objNewDto.getTelefone2() != null) {
			cliente.getTelefones().add(objNewDto.getTelefone2());
		}
		if (objNewDto.getTelefone3() != null) {
			cliente.getTelefones().add(objNewDto.getTelefone3());
		}
		return cliente;
	}

	public void updateCliente(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
}
