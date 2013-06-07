import groovy.sql.Sql


def cli = new CliBuilder(usage: 'ParseEmailAlert.groovy -[hndupo]')
cli.with{
	h longOpt: 'help', 'the options usage'
	n longOpt: 'database node', args: 1, argName: 'hostName', 'database node'
	d longOpt: 'database', args: 1, argName: 'databaseName', 'database name'
	u longOpt: 'user', args: 1, argName: 'user', 'the database user. root if not specified'
	p longOpt: 'password', args: 1, argName: 'password', '''the database password. '' if not specified'''
	o longOpt: 'outout directory', args: 1, argName: 'outputDirectory', 'the output directory'
	}

def options = cli.parse(args)

if (options.h){
	cli.usage()
	return
}

hostName=options.n?: 'localhost'
databaseName=options.d?options.d : 'test'
user=options.u?:'root'
password=options.p?:''
outputDirectory=options.o?:"/jobs/test"

connectString = "jdbc:mysql://${hostName}:3306/test"
println connectString

sqlForCollectSaveSearch='''
select v.`visitor_login_id`, s.`name`, s.`ntf_frequency` from visitor v, saved_search s 
   where s.`visitor_uid` = v.`visitor_uid` and s.`search_type`='saved' and s.`ntf_frequency` is not NULL 
   and v.`visitor_login_id` is not NULL
'''
emailAlertData=[]

def handleRow={row ->
	data =  row.toRowResult().values().collect{it?.replace(",", "+")?.replace(" ", "")}.join(",\t")
	emailAlertData << data
	println data 
}	

def writeToFile() {
	fileName = new Date().format('yyyy-MM-dd-HH-mm-ss')
	File f = new File("${outputDirectory}/${fileName}.csv")
	emailAlertData.each{f << "${it}\n"}
}

def sql = Sql.newInstance("jdbc:mysql://${hostName}:3306/${databaseName}", "${user}", "${password}", 'com.mysql.jdbc.Driver')
sql.eachRow(sqlForCollectSaveSearch, handleRow)
writeToFile()
