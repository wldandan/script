import groovy.sql.Sql


def cli = new CliBuilder(usage: 'UpdateListing.groovy -[hndupo]')
cli.with{
    h longOpt: 'help', 'the options usage'
    n longOpt: 'database node', args: 1, argName: 'hostName', 'database node'
    d longOpt: 'database', args: 1, argName: 'databaseName', 'database name'
    u longOpt: 'user', args: 1, argName: 'user', 'the database user. root if not specified'
    p longOpt: 'password', args: 1, argName: 'password', '''the database password. '' if not specified'''
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

connectString = "jdbc:jtds:sqlserver://${hostName}:1433/${databaseName}"
println connectString

sqlUpdateListing='''
update ListingsPersisted set DataOrdinamento=GETDATE()-3 where CODICEANNUNCIO =1486769
update IMMOBILI set DATASCADENZA = getdate() + 13, INDIRIZZOVISIBILESULSITO = 0, PuntiUPdateDt = getdate(), FlgPrezzoVisibile = 0 where CODICEANNUNCIO =1486769
select * from ListingsPersisted where CODICEANNUNCIO =1486769
'''

/*
select DPendDt from ListingsPersisted where CODICEANNUNCIO =1486769
update ListingsPersisted set DataOrdinamento=GETDATE()-3, featuredTypeName='Premiere Property', DPendDt=GETDATE()+3 where CODICEANNUNCIO =1486769
*/

def handleRow={row ->
    data =  row.toRowResult().values().collect{it}
    println data
}


def sql = Sql.newInstance("${connectString};appName=jetwire;loginTimeout=30;useLOBs=false;charset=utf8", "${user}", "${password}", 'net.sourceforge.jtds.jdbc.Driver')
sql.eachRow(sqlUpdateListing, handleRow)
