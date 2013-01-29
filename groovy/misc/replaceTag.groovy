import java.util.regex.Matcher
import java.util.regex.Pattern

path=""
def replaceUrl(String s) {
    def mobilePattern = /.*<mobile:import url=\"(.*)\">.*/
    def cPattern = '''<c:import url="${ path }"'''
    def mobileEndPattern = /.*<\/mobile:import>.*/

    if ((m = s =~ mobilePattern)){
        path = m[0][1]
        println path
        return ""
    }
    else if (s.indexOf(cPattern) > -1){
        rs=s.replace('''${ path }''', path)
        return rs
    }
    else if ((m = s =~ mobileEndPattern)){
        return "";
    }
    return s;
}

def process(fileName){
    println("processing " + fileName)
    File file = new File(fileName)
    rs = file.readLines().inject([]){ list, it ->
        rs = replaceUrl(it).toString()
        if (!(rs.empty)){
            list << rs
        }
        list
    }
    writeToFile(fileName, rs)
}

def writeToFile(fileName, data) {
  p = new File(fileName).newWriter();
  data.each{
    p.println(it.toString())
  }
}

process("/Users/raven/source/scripts/404.jsp")







