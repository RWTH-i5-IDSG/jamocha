(deffunction fipa-sl-request-handler
    (?factid)
    (printout t "Ein Request ist gekommen: " ?factid)
    (printout t (fact-slot-value ?factid content))
    (if (eq "fipa-sl" (str-lower (fact-slot-value ?factid language))) then (bind ?clipsCode
	    	(sl2clips
	    		(fact-slot-value ?factid performative)
	    		(fact-slot-value ?factid content)
	    	)
	    )
	    (printout t "got sl code and parsed it: " ?clipsCode)
    else (bind ?clipsCode
	 		(fact-slot-value ?factid content)
	 	)
	    (printout t "got clips-code")
	)
    (bind ?result 
    	(eval ?clipsCode)
    )
    (printout t ?result)
    (agent-send-message 
    	(fact-slot-value ?factid sender) 
    	(fact-slot-value ?factid reply-to)
    	(fact-slot-value ?factid performative)
    	(str-cat (fact-slot-value ?factid content) crlf ?result) 
    	(fact-slot-value ?factid language)
    	(fact-slot-value ?factid encoding)
    	(fact-slot-value ?factid ontology)
    	(fact-slot-value ?factid protocol)
    	(fact-slot-value ?factid conversation-id)
    	(fact-slot-value ?factid reply-with)
    	""
    	NIL
    )
)    

(assert 
    (agent-performative 
        (code 16)
        (handlerfunction "fipa-sl-request-handler")
    )
)