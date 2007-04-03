(deftemplate jdbc_test (slot id) (slot test_fact))
(assert
    (jdbclink
        (ConnectionName "testcon")
        (TableName "jamocha_facts")
        (TemplateName "jdbc_test")
        (Username "tim")
        (Password "jamocha")
        (JDBCurl "jdbc:postgresql://localhost:5432/tim")
        (JDBCdriver "org.postgresql.Driver")
    )
)
(jdbclink 2 "import" "foo")
