(deftemplate templ (slot a) (slot b) (slot c) )
(assert (jdbclink (JDBCdriver "com.mysql.jdbc.Driver") (ConnectionName "h") (TableName "test") (TemplateName "templ") (Username "jamocha") (Password "geheim") (JDBCurl "jdbc:mysql://134.130.113.67:65306/jamocha")))
(jdbclink 2 "import" "foo")