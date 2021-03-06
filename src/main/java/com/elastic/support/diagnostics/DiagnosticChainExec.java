package com.elastic.support.diagnostics;

import com.elastic.support.SystemProperties;
import com.elastic.support.chain.Chain;
import com.elastic.support.util.JsonYamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DiagnosticChainExec {

   private static Logger logger = LoggerFactory.getLogger(DiagnosticChainExec.class);

   public void runDiagnostic(DiagnosticContext context) {

      try {
         Map<String, Object> diags = JsonYamlUtils.readYamlFromClasspath("diags.yml", true);
         if (diags.size() == 0) {
            logger.error("Required config file diags.yml was not found. Exiting application.");
            throw new RuntimeException("Missing diags.yml");
         }

         context.setConfig(diags);

         Map<String, Object> chains = JsonYamlUtils.readYamlFromClasspath("chains.yml", false);
         if (chains.size() == 0) {
            logger.error("Required config file chains.yml was not found. Exiting application.");
            throw new RuntimeException("Missing diags.yml");
         }

         String diagType = context.getInputParams().getDiagType();

         List<String> chain = (List) chains.get(diagType);

         if (diagType.equals(SystemProperties.LOGSTASH_DIAG)) {
            context.setDiagName(SystemProperties.LOGSTASH_DIAG + "-" + SystemProperties.ES_DIAG);
         }

         Chain diagnostic = new Chain(chain);
         diagnostic.execute(context);
      } catch (Exception e) {
         logger.error("Error encountered running diagnostic. See logs for additional information.  Exiting application.", e);
         throw new RuntimeException("Diagnostic runtime error", e);
      }

   }

}