package br.com.miranda.evento;

import br.com.miranda.service.ImpressaoSvr;
import br.com.sankhya.extensions.eventoprogramavel.EventoProgramavelJava;
import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.TransactionContext;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.ws.ServiceContext;

public class DanfeSimplificadoFilaConf implements EventoProgramavelJava {
	
	public void danfeSimplificado(PersistenceEvent event) throws Exception {
		DynamicVO camposCtx = (DynamicVO) event.getVo();
		ServiceContext ctx = ServiceContext.getCurrent();
		
		Integer codemp = camposCtx.asInt("CODEMP");
		
		if(codemp != 5) {
			return;
		}

		try {
			String statusNfeAntigo = (String) event.getModifingFields().getOldValue("STATUSNFE");
			String statusNfeNovo = camposCtx.asString("STATUSNFE");
			
			if ("V".equalsIgnoreCase(camposCtx.asString("TIPMOV")) && "A".equalsIgnoreCase(statusNfeNovo)
					&& !statusNfeNovo.equalsIgnoreCase(statusNfeAntigo)) {
				
				ImpressaoSvr.imprimir(camposCtx.asBigDecimal("NUNOTA"), ctx);
			}

		} catch (Exception e) {
			System.out.println("ERRO NO EVENTO DE IMPRESSÃO DE DANFE SIMPLIFICADO NA FILA DE CONFERENCIA. EXCEPTION -> " + e.getMessage());
			e.printStackTrace();
		}
	}


	@Override
	public void afterDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterUpdate(PersistenceEvent event) throws Exception {
		danfeSimplificado(event);
	}

	@Override
	public void beforeCommit(TransactionContext tranCtx) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeDelete(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
