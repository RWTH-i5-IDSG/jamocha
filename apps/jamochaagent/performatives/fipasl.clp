(deffunction fipa-sl-request-handler "Handles incoming requests in FIPA-SL." (?factid)
    (bind ?clipsCode (fact-slot-value ?factid content))
    ; if we have content in fipa-sl we parse it and translate it to CLIPS
    (if
    	(eq "fipa-sl" (str-lower (fact-slot-value ?factid language)))
     then
     	(bind ?clipsCode
	    	(sl2clips
	    		(fact-slot-value ?factid performative)
	    		?clipsCode
	    	)
	    )
    )
    (bind ?result 
    	(eval ?clipsCode)
    )
    (if
    	(eq "fipa-sl" (str-lower (fact-slot-value ?factid language)))
     then
     	(bind ?result (clips2sl ?result))
    )
    (printout t ?result)
    (agent-send-message 
    	(fact-slot-value ?factid sender) 
    	(fact-slot-value ?factid reply-to)
    	"inform"
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
        (performative "request")
        (handlerfunction "fipa-sl-request-handler")
    )
)