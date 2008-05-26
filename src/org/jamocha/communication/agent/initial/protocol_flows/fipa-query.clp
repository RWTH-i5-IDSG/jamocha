(bind ?fipa-query-0 (assert
	(ip-state
		(protocol-name "fipa-query") 
		(state-name 0)
	)
))

; got query-if or query-ref
(bind ?fipa-query-1 (assert
	(ip-state
		(protocol-name "fipa-query") 
		(state-name 1)
	)
))

(assert
	(ip-transition
		(from-state ?fipa-query-0)
		(to-state ?fipa-query-1)
		(speechact "query-if")
		(agent-type "initiator")
	)
)

(assert
	(ip-transition
		(from-state ?fipa-query-0)
		(to-state ?fipa-query-1)
		(speechact "query-ref")
		(agent-type "initiator")
	)
)

; refused
(bind ?fipa-query-2 (assert
	(ip-state
		(protocol-name "fipa-query") 
		(state-name 2)
	)
))


(assert
	(ip-transition
		(from-state ?fipa-query-1)
		(to-state ?fipa-query-2)
		(speechact "refuse")
		(agent-type "participant")
	)
)

; agreed
(bind ?fipa-query-3 (assert
	(ip-state
		(protocol-name "fipa-query") 
		(state-name 3)
	)
))


(assert
	(ip-transition
		(from-state ?fipa-query-1)
		(to-state ?fipa-query-3)
		(speechact "agree")
		(agent-type "participant")
	)
)

; failed
(bind ?fipa-query-4 (assert
	(ip-state
		(protocol-name "fipa-query") 
		(state-name 4)
	)
))


(assert
	(ip-transition
		(from-state ?fipa-query-3)
		(to-state ?fipa-query-4)
		(speechact "failure")
		(agent-type "participant")
	)
)

; informed
(bind ?fipa-query-5 (assert
	(ip-state
		(protocol-name "fipa-query") 
		(state-name 5)
	)
))


(assert
	(ip-transition
		(from-state ?fipa-query-3)
		(to-state ?fipa-query-5)
		(speechact "inform")
		(agent-type "participant")
	)
)

