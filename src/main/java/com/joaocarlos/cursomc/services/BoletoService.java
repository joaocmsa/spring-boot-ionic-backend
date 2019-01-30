package com.joaocarlos.cursomc.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.joaocarlos.cursomc.domain.PagamentoComBoleto;

@Service
public class BoletoService {

	public void preencherPagamentoComBoleto(PagamentoComBoleto pagamento, Date dataDoPedido) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataDoPedido);
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		pagamento.setDataVencimento(calendar.getTime());
	}
}
