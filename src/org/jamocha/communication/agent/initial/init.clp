; ===================================================
; Initial data that is essential to run the JamochaAgent.
; ===================================================

;(defmodule Agent)

(batch "internal://communication/agent/initial/templates.clp")

(batch "internal://communication/agent/initial/functions.clp")

(batch "internal://communication/agent/initial/rules.clp")

(batch "internal://communication/agent/initial/protocol_flow.clp")

(batch "internal://communication/agent/initial/protocol_flows/fipa-query.clp")

(batch "internal://communication/agent/initial/protocol_flows/fipa-request.clp")