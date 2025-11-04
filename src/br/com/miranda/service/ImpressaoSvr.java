package br.com.miranda.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.modelcore.comercial.util.print.PrintManager;
import br.com.sankhya.modelcore.comercial.util.print.converter.PrintConversionService;
import br.com.sankhya.modelcore.comercial.util.print.model.PrintInfo;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.Report;
import br.com.sankhya.modelcore.util.ReportManager;
import br.com.sankhya.sps.enumeration.DocTaste;
import br.com.sankhya.sps.enumeration.DocType;
import br.com.sankhya.ws.ServiceContext;
import net.sf.jasperreports.engine.JasperPrint;

public class ImpressaoSvr {

	public static void imprimir(BigDecimal nunota, ServiceContext ctx) throws SQLException {
		EntityFacade dwfEntityFacade;
		JdbcWrapper jdbc = null;

		dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		jdbc = dwfEntityFacade.getJdbcWrapper();
		jdbc.openSession();

		try {
			dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
			jdbc = dwfEntityFacade.getJdbcWrapper();
			jdbc.openSession();
			
			Map<String, Object> param = new HashMap<>();
			param.put("NUNOTA", nunota);

			String printerName = "?";
			String jobDescription = "Impressao por job";
			int copies = 1;

			BigDecimal userId = AuthenticationInfo.getCurrent().getUserID();
			String userName = "SUP";
			BigDecimal codEmp = BigDecimal.ONE;
			String idDocumento = "0";

			PrintManager printManager = PrintManager.getInstance();

			Report report = ReportManager.getInstance().getReport(new BigDecimal(292), dwfEntityFacade);
			JasperPrint jasperPrint = report.buildJasperPrint(param, jdbc.getConnection());

			byte[] conteudo = PrintConversionService.getInstance().convert(jasperPrint, byte[].class);

			PrintInfo printInfo = new PrintInfo(conteudo, DocTaste.JASPER, DocType.RELATORIO, printerName,
					jobDescription, copies, userId, userName, codEmp, idDocumento);

			printManager.print(printInfo);

		} catch (Exception e) {
			System.out.println("ERRO AO IMPRIMIR RELATÓRIO -> "+ e.getMessage());
			e.getStackTrace();
		} finally {
			JdbcWrapper.closeSession(jdbc);
		}
	}
}
