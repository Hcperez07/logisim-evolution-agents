# Cómo trabajar un circuito completo en AgentWorkSpace

## Objetivo
Esta carpeta contiene material mínimo para que un agente pueda construir, validar y exportar un circuito `.circ` de punta a punta sin romper compatibilidad.

## Archivos incluidos
- `agent_workflow.md`: flujo recomendado de CLI para validación/exportación.
- `agent_kill_strategy.md`: estrategia de timeout/kill y códigos de salida esperados.
- `docs.md`: referencia general de documentación del proyecto.

## Flujo recomendado de trabajo
1. Crear/editar el `.circ` en iteraciones pequeñas.
2. Ejecutar validación:
   - `java -jar logisim-evolution.jar --timeout-ms 30000 --validate-circ proyecto.circ`
3. Si necesitas parsing automático de resultados:
   - añadir `--json`.
4. Exportar imagen del circuito/subcircuito para revisión visual:
   - `java -jar logisim-evolution.jar --timeout-ms 30000 --export-image proyecto.circ ALU png out/ALU.png`
5. Repetir hasta converger (validación limpia + salida visual correcta).

## Reglas de compatibilidad
- No inventar ni extender sintaxis XML de `.circ`.
- Mantener estructura compatible con el parser actual de Logisim-evolution.
- Si hace falta metadata adicional del agente, guardar en archivos externos (JSON/MD), no dentro del `.circ`.

## Checklist de entrega para agentes
- [ ] Validación `--validate-circ` en exit code `0`.
- [ ] Al menos una exportación de imagen exitosa.
- [ ] Manejo explícito de error (archivo inválido/inexistente) verificado.
- [ ] Evidencia de comandos y resultados guardada en logs.
