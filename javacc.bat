@echo off
set javacc=java -cp c:\\dev\\libs\\javacc\\bin\\javacc.jar
%javacc% jjtree src/org/jamocha/languages/clips/parser/SFP.jjt
%javacc% javacc generated/org/jamocha/languages/clips/parser/generated/SFP.jj
pause