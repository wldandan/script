import groovy.sql.Sql

def log(msg) {
    System.out.println ("[${new Date().getDateTimeString()}] ${msg}")
}


def cli = new CliBuilder(usage: 'ParseEmailAlert.groovy -[hndupo]')
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

hostName=options.n?:'172.20.55.229'
databaseName=options.d?options.d : 'email_alert'
user=options.u?:'root'
password=options.p?:''

serverConn="jdbc:mysql://${hostName}:3306/email_alert"
System.out.println serverConn

sqlCheckAlert='''
select * from recent_alerted_listing where listing_id = '1486769'
'''


exist=false

def handleRow={row ->
    System.out.println(row.toRowResult())
    exist = !row.toRowResult().isEmpty()
}
System.out.println ("[${new Date().getDateTimeString()}] waiting for email alert data to be injected!")

System.out.println serverConn
def sql = Sql.newInstance(serverConn, "${user}", "${password}", 'com.mysql.jdbc.Driver')
System.out.println sql
sql.eachRow(sqlCheckAlert, handleRow)

while (!exist){
    sleep(1000)
    sql.eachRow(sqlCheckAlert, handleRow)
}

sql.execute '''delete from recent_alerted_listing;'''
System.out.println ("[${new Date().getDateTimeString()}] delete recent alert listing!")

sql.execute '''
update email_alert set send_time = now();
'''
System.out.println ("[${new Date().getDateTimeString()}] update send email time!")
System.out.println ("[${new Date().getDateTimeString()}] sending out email!")

sql.execute '''delete from recent_alerted_listing;'''
System.out.println ("[${new Date().getDateTimeString()}] email alert has been sent!")
