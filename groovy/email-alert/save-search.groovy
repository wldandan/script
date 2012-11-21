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
    m longOpt: 'email', args: 1, argName: 'email', '''the email address to retrieve visitor id'''
}

def options = cli.parse(args)

if (options.h){
    cli.usage()
    return
}

hostName=options.n?: 'localhost'
databaseName=options.d?options.d : 'rea'
user=options.u?:'root'
password=options.p?:''
email=options.m?:'wang.lei@rea-group.com'

serverConn="jdbc:mysql://${hostName}:3306/${databaseName}"
println serverConn


def sql = Sql.newInstance(serverConn, "${user}", "${password}", 'com.mysql.jdbc.Driver')
row = sql.firstRow('SELECT UUID_SHORT() as id;')
search_id=row.id

println "generating uuid as save_search id: ${search_id}"

row = sql.firstRow("SELECT VISITOR_UID FROM VISITOR WHERE VISITOR_LOGIN_ID=?", [email])
visitor_uid=row.VISITOR_UID
println "retrieving visitor id: ${visitor_uid} by ${email}"

encode_properties='''
{"listingType":"buy","resolvedSurroundingLocationCodes":"|IT-PUG-074007|","where":"Selva di Fasano, Fasano, BR, Puglia","resolvedLocations":"|Selva di Fasano, Fasano, BR, Puglia|","searchView":"list","resolvedLocationCodes":"|Z-31495|","userWhere":"selva di fasano, fasano br","channel":"buy","domain":"localhost","searchUrl":"/vendita-residenziale/in-selva+di+fasano%2c+fasano%2c+br%2c+puglia/lista-1"}
'''
search_type='saved'
ntf_frequency='immediately'
save_search_name='tmp-save-search-name'

def params=[search_id,visitor_uid,search_type,save_search_name,encode_properties,ntf_frequency]
params.each{
  println "current item is ${it}"
}

sql.execute 'insert into saved_search(search_id,visitor_uid,search_type,name,encoded_properties,ntf_frequency) values (?, ?, ?, ?, ?, ?)', params


/*
sqlAddSaveSearch='''
select * from recent_alerted_listing where listing_id = '1486769'
'''


exist=false

def handleRow={row ->
    System.out.println(row.toRowResult())
    exist = !row.toRowResult().isEmpty()
}
System.out.println ("[${new Date().getDateTimeString()}] waiting for email alert data to be injected!")


def sql = Sql.newInstance(serverConn, "${user}", "${password}", 'com.mysql.jdbc.Driver')
sql.eachRow(sqlCheckAlert, handleRow)

while (!exist){
    sleep(1000)
    sql.eachRow(sqlCheckAlert, handleRow)
}

sql.execute '''
delete from recent_alerted_listing;
'''
System.out.println ("[${new Date().getDateTimeString()}] delete recent alert listing!")

sql.execute '''
update email_alert set send_time = now();
'''
System.out.println ("[${new Date().getDateTimeString()}] update send email time!")
System.out.println ("[${new Date().getDateTimeString()}] sending out email!")

//sql.eachRow(sqlCheckAlert, handleRow)
//while (exist){
//    sleep(1000)
//    sql.eachRow(sqlCheckAlert, handleRow)
//}
sql.execute ''' 
delete from recent_alerted_listing;
'''
System.out.println ("[${new Date().getDateTimeString()}] email alert has been sent!")
*/
