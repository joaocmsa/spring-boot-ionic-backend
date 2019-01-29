package com.joaocarlos.cursomc.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.joaocarlos.cursomc.domain.Cliente;
import com.joaocarlos.cursomc.domain.enums.TipoCliente;
import com.joaocarlos.cursomc.dto.ClienteNewDTO;
import com.joaocarlos.cursomc.repositories.ClienteRepository;
import com.joaocarlos.cursomc.resources.exception.FieldMessage;
import com.joaocarlos.cursomc.services.validation.utils.BR;

public class ClienteInsertValidator implements ConstraintValidator<ClienteInsert, ClienteNewDTO> {
	
	@Autowired
	private ClienteRepository clienteRepository; 
	
	@Override
	public void initialize(ClienteInsert ann) {
	}

	@Override
	public boolean isValid(ClienteNewDTO objNewDto, ConstraintValidatorContext context) {
		
		List<FieldMessage> lista = new ArrayList<>();
		
		if(objNewDto.getTipo().equals(TipoCliente.PESSOAFISICA.getCod()) && !BR.isValidCPF(objNewDto.getCpfOuCnpj()) ) {
			lista.add(new FieldMessage("cpfOuCnpj", "CPF inválido."));
		}
		
		if(objNewDto.getTipo().equals(TipoCliente.PESSOAJURIDICA.getCod()) && !BR.isValidCNPJ(objNewDto.getCpfOuCnpj()) ) {
			lista.add(new FieldMessage("cpfOuCnpj", "CNPJ inválido."));
		}
		
		Cliente cliente = clienteRepository.findByEmail(objNewDto.getEmail());
		if(cliente != null) {
			lista.add(new FieldMessage("email", "E-mail já existente."));
		}
		
		for (FieldMessage e : lista) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return lista.isEmpty();
	}
}
