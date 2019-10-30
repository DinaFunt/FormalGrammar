# FormalGrammar

## 1. Богданов Евгений, LBA -> КЗ

### сборка: (из папки BogdanovEvgenij)
	javac -sourcepath ./src -d bin src/Main.java
	
### запуск: (из папки BogdanovEvgenij)
	0. java -classpath ./bin Main [mode] [num] [inputfile] -- общий вид команды
	1. java -classpath ./bin Main -b -- построение грамматики
	2. java -classpath ./bin Main -d 5 final.txt -- вывод числа 5

## 2. Дина Фунт, MT -> Свободная грамматика

### сборка: (из папки DinaFunt)
	javac -sourcepath ./src -d bin src/Main.java
	
### запуск: (из папки DinaFunt)
	java -classpath ./bin Main [number for check]
	ex: java -classpath ./bin Main 5 	     -- проверка и вывод числа 5
	
