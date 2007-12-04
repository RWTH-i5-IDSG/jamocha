; ===================================================
; Initial data that is essential to run the JamochaAgent.
; ===================================================

;(defmodule Agent)

(batch (str-cat ?init-folder "templates.clp"))

(batch (str-cat ?init-folder "functions.clp"))

(batch (str-cat ?init-folder "rules.clp"))

(batch (str-cat ?init-folder "protocol_flow.clp"))