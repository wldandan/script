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

hostName=options.n?: '172.20.56.86'
databaseName=options.d?options.d : 'JetwireTest'
user=options.u?:'casa'
password=options.p?:'casa'
outputDirectory=options.o?:"/jobs/email-alert"

connectString = "jdbc:jtds:sqlserver://${hostName}:1433/${databaseName}"
println connectString

sqlForCollectSaveSearch='''
select * from IMMOBILI WHERE CODICEANNUNCIO = '1486769'
'''

sqlUpdateListing='''
select * from IMMOBILI WHERE CODICEANNUNCIO = '1486769'
update IMMOBILI set DESCRIZIONE = 'test' where CODICEANNUNCIO = '1486769'
'''


def handleRow={row ->
    data =  row.toRowResult().values().collect{it}
    println data
}


def sql = Sql.newInstance("${connectString};appName=jetwire;loginTimeout=30;useLOBs=false;charset=utf8", "${user}", "${password}", 'net.sourceforge.jtds.jdbc.Driver')
sql.eachRow(sqlUpdateListing, handleRow)
//writeToFile()