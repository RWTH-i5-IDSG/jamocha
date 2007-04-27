(deftemplate templ (slot a) (slot b) (slot c) (slot foo) )
(jdbclink-init)
(bind ?mylink 
	(assert
		(jdbclink 
				(JDBCdriver "com.mysql.jdbc.Driver") 
				(ConnectionName "db") 
				(TableName "test") 
				(TemplateName "templ") 
				(Username "jamocha") 
				(Password "geheim") 
				(JDBCurl "jdbc:mysql://134.130.113.67:3306/jamocha")
		)
	)
)

(bind ?myfilter
	(assert
		(jdbccondition
			(SlotName "foo")
			(BooleanOperator ">")
			(Value 2007-04-27 19:00+1)
		)
	)
)


(jdbclink ?mylink "import" (create$ ?myfilter)) 
