import groovy.sql.Sql

def log(msg) {
    System.out.println ("[${new Date().getDateTimeString()}] ${msg}")
}


def cli = new CliBuilder(usage: 'Send generic request -[hndupo]')
cli.with{
    h longOpt: 'help', 'the options usage'
    r longOpt: 'remove', 'remove saved searches'
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
isDeleteOption=options.r?:false

serverConn="jdbc:mysql://${hostName}:3306/${databaseName}"
println "connecting the server ${serverConn}"

def sql = Sql.newInstance(serverConn, "${user}", "${password}", 'com.mysql.jdbc.Driver')
if (isDeleteOption) {
    println "deleting all generic request..."
    sql.execute 'delete from saved_search where search_type="generic_request"'
    println "Finished."
    return
}
row = sql.firstRow('SELECT UUID_SHORT() as id;')
search_id=row.id
println "generating uuid as save_search id: ${search_id}"

row = sql.firstRow("SELECT VISITOR_UID FROM VISITOR WHERE VISITOR_LOGIN_ID=?", [email])
visitor_uid=row.VISITOR_UID
println "retrieving visitor id: ${visitor_uid} by ${email}"

encode_properties='''{"category":"residenziale","resolvedLocationCodes":"|Z-31495|","preferredState":"pug","channel":"buy","resolvedSurroundingLocationCodes":"|Z-31495|","minHouseSize":"200","resolvedLocations":"|Selva di Fasano, Fasano, BR, Puglia|","where":"Selva di Fasano, Fasano, BR,Puglia","listingType":"buy","propertyType":"Castello","searchView":"list","minPrice":"500000","domain":"localhost","maxPrice":"1000000","maxHouseSize":"400","searchUrl":"/vendita-residenziale/immobile-castello-dimensione-200-400-per-500000-1000000-in-selva+di+fasano%2c+fasano%2c+br%2c+puglia/lista-1?preferredState=pug","firstName":"wang","lastName":"lei","email":"wang.lei@rea-group.com","telephone":"","message":""}'''
search_type='generic_request'
save_search_name='tmpgeneric_request'

def params=[search_id,visitor_uid,search_type,save_search_name,encode_properties]

sql.execute 'insert into saved_search(search_id,visitor_uid,search_type,name,encoded_properties) values (?, ?, ?, ?, ?)', params
println "save generic request successfully!"

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
