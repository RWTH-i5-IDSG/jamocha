(deftemplate templ (slot a) (slot b) (slot c) )
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
				(JDBCurl "jdbc:mysql://134.130.113.67:65306/jamocha")
		)
	)
)

(jdbclink ?mylink "import" "foo") 
