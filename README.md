# Evento Java - Impressão Automática de DANFE Simplificado na Fila de Conferência

Este projeto contém um **evento programável Java para o ERP Sankhya**, desenvolvido para automatizar a **impressão do DANFE Simplificado** exclusivamente na **fila de conferência**.  
Em todos os demais pontos do sistema, permanece o comportamento padrão, utilizando o **DANFE 4.0 (A4)**.

---

## 🎯 Objetivo

Resolver a limitação de impressão automática do DANFE Simplificado dentro da conferência, sem impactar as demais operações e rotinas de impressão do ERP Sankhya.

---

## ⚙️ Funcionamento

O evento intercepta a finalização da conferência, criação de uma nota (TIPMOV = 'V') com STATUSNFE = 'A' e executa a impressão automática do **DANFE Simplificado**, desde que o processo esteja na **fila de conferência**.  
Para os demais fluxos, o **DANFE padrão (A4)** continua sendo utilizado normalmente.

---

## 🧩 Configuração no ERP Sankhya

Para o evento funcionar corretamente, siga os passos abaixo:

1. **Definir o DANFE padrão como A4:**
   - Acesse: `Empresa > Comercial > Preferências > NF-e/NFC-e`
   - Configure o **DANFE A4** como padrão na opção Relatório Formatado Danfe.

2. **Configurar as TOPs referentes ao Marketplace:**
   - Acesse: `Comercial > Cadastros > Tipos de Operação (TOP)`
   - Na aba **Impressão**, altere a opção de **Impressão** para **Manual**.
   - Isso garante que, ao finalizar a conferência, apenas o evento Java será responsável por disparar a impressão automática do DANFE Simplificado.

3. **Criar o evento na tabela TFGCAB:**
   - Crie o evento programável e associe o código conforme este repositório.
   - Realize o filtro de tela para o evento ser disparado somente na tela de fila de conferência.

4. **Subir o módulo Java no servidor Sankhya.**

5. **Importar o relatório do DANFE Simplificado:**
   - O relatório foi desenvolvido de acordo com as normas técnicas oficiais.

---
## 🧠 Exemplo de Código

### 🔹 Chamada do evento
```java
public void afterUpdate(PersistenceEvent event) throws Exception {
    danfeSimplificado(event);
}
```

### 🔹Método danfeSimplificado
```java
public void danfeSimplificado(PersistenceEvent event) throws Exception {
    DynamicVO camposCtx = (DynamicVO) event.getVo();
    ServiceContext ctx = ServiceContext.getCurrent();
    
    Integer codemp = camposCtx.asInt("CODEMP");
    
    if (codemp != 5) { // Apenas empresa 5 utiliza DANFE simplificado
        return;
    }

    try {
        String statusNfeAntigo = (String) event.getModifingFields().getOldValue("STATUSNFE");
        String statusNfeNovo = camposCtx.asString("STATUSNFE");
        
        if ("V".equalsIgnoreCase(camposCtx.asString("TIPMOV")) 
            && "A".equalsIgnoreCase(statusNfeNovo)
            && !statusNfeNovo.equalsIgnoreCase(statusNfeAntigo)) {
            
            ImpressaoSvr.imprimir(camposCtx.asBigDecimal("NUNOTA"), ctx);
        }

    } catch (Exception e) {
        System.out.println("ERRO NO EVENTO DE IMPRESSÃO DE DANFE SIMPLIFICADO NA FILA DE CONFERÊNCIA -> " + e.getMessage());
        e.printStackTrace();
    }
}
```
###🔹 Método imprimir()
```java
public static void imprimir(BigDecimal nunota, ServiceContext ctx) throws SQLException {
    EntityFacade dwfEntityFacade;
    JdbcWrapper jdbc = null;

    dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
    jdbc = dwfEntityFacade.getJdbcWrapper();
    jdbc.openSession();

    try {
        Map<String, Object> param = new HashMap<>();
        param.put("NUNOTA", nunota);

        String printerName = "?";
        String jobDescription = "Impressão por job";
        int copies = 1;

        BigDecimal userId = AuthenticationInfo.getCurrent().getUserID();
        String userName = "SUP";
        BigDecimal codEmp = BigDecimal.ONE;
        String idDocumento = "0";

        PrintManager printManager = PrintManager.getInstance();

        // Informe o código do relatório DANFE Simplificado cadastrado na tela de Relatórios Formatados
        Report report = ReportManager.getInstance().getReport(new BigDecimal(292), dwfEntityFacade);
        JasperPrint jasperPrint = report.buildJasperPrint(param, jdbc.getConnection());

        byte[] conteudo = PrintConversionService.getInstance().convert(jasperPrint, byte[].class);

        PrintInfo printInfo = new PrintInfo(conteudo, DocTaste.JASPER, DocType.RELATORIO, printerName,
                jobDescription, copies, userId, userName, codEmp, idDocumento);

        printManager.print(printInfo);

    } catch (Exception e) {
        System.out.println("ERRO AO IMPRIMIR RELATÓRIO -> " + e.getMessage());
        e.printStackTrace();
    } finally {
        JdbcWrapper.closeSession(jdbc);
    }
}
```

---

## ✅ Resultado Esperado

Após todas as configurações:
- A finalização de uma conferência dispara automaticamente o **DANFE Simplificado**;
- Em qualquer outro ponto do sistema, o **DANFE 4.0 (A4)** continua sendo utilizado como padrão.

---

## 🧠 Tecnologias e versões utilizadas

- **Java** (Evento Programável Sankhya)
- **ERP Sankhya** (versões recentes compatíveis com eventos programáveis)
- **Relatório Jasper (JRXML)** para DANFE Simplificado
- **Lib's do Sankhya que precisei importar no projeto**: Jape-4.23b.151, jasperreports-1.1.0, mge-modelcore-4.23.b151, print-service-base, SankhyaW-extencions, sanws.

---

## 📜 Licença

Este projeto é distribuído sob a [Licença MIT](./LICENSE).  
Você pode usar, modificar e distribuir livremente, desde que mantenha os créditos originais.

---

## 🤝 Contribuições

Contribuições, melhorias e adaptações são bem-vindas!  
Sinta-se à vontade para abrir um *Pull Request* ou relatar *issues* no repositório.

---

## 📣 Autor

**Desenvolvido por:** [Isaac Aguiar](https://github.com/isaacaguiar)  
**Propósito:** Auxiliar a comunidade de desenvolvedores Sankhya a contornar limitações de impressão automática de DANFE Simplificado.

