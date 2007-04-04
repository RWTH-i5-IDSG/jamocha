(deftemplate fibonacciconfig (slot max))

(bind ?config 
	(assert
		(fibonacciconfig 
				(max "1000") 
		)
	)
)

(iteratorimporter "org.jamocha.sampleimplementations.DeffactFibonacciIterator" ?config) 
