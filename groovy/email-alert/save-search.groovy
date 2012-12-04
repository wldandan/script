import groovy.sql.Sql

def log(msg) {
    System.out.println ("[${new Date().getDateTimeString()}] ${msg}")
}


def cli = new CliBuilder(usage: 'ParseEmailAlert.groovy -[hndupo]')
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
println serverConn

def sql = Sql.newInstance(serverConn, "${user}", "${password}", 'com.mysql.jdbc.Driver')

row = sql.firstRow("SELECT VISITOR_UID FROM VISITOR WHERE VISITOR_LOGIN_ID=?", [email])
visitor_uid=row.VISITOR_UID
println "retrieving visitor id: ${visitor_uid} by ${email}"

if (isDeleteOption) {
    println "deleting all saved searches..."
    sql.execute "delete from saved_search where search_type='saved' and visitor_uid=${visitor_uid}"
    println "Clean up all email alert for current user ${email}"
    return
}

println "waiting for generating uuid as save_search id"
row = sql.firstRow('SELECT UUID_SHORT() as id;')
search_id=row.id
println "generating uuid as save_search id: ${search_id}"


encode_properties='''{"listingType":"buy","category":"residenziale","propertyType":"Castello","searchView":"list","resolvedLocationCodes":"|Z-31495|","preferredState":"pug","minPrice":"500000","channel":"buy","domain":"localhost","resolvedSurroundingLocationCodes":"|Z-31495|","maxPrice":"1000000","minHouseSize":"200","where":"Selva di Fasano, Fasano, BR, Puglia","resolvedLocations":"|Selva di Fasano, Fasano, BR, Puglia|","maxHouseSize":"400","searchUrl":"/vendita-residenziale/immobile-castello-dimensione-200-400-per-500000-1000000-in-selva+di+fasano%2c+fasano%2c+br%2c+puglia/lista-1?preferredState=pug"}'''
search_type='saved'

ntf_frequency='immediately'
println "the email frequency is ${ntf_frequency}"

save_search_name='Selva di Fasano, FASANO BR'

def params=[search_id,visitor_uid,search_type,save_search_name,encode_properties,ntf_frequency]

sql.execute 'insert into saved_search(search_id,visitor_uid,search_type,name,encoded_properties,ntf_frequency,usage_timestamp) values (?, ?, ?, ?, ?, ?,now())', params
