
deleteFlag = 0
whenFlag=0

def shouldRemove(String s){
    def highPattern = "mobileLevel eq 'high'"
    def whenPatternStart = "<c:when>"
    def whenPatternEnd = "</c:when>"

    if (s.indexOf(highPattern) > -1)
        deleteFlag = 1

    if (deleteFlag == 1){

        if (whenFlag==0){
            deleteFlag = 0;
        }

        if (s.indexOf(whenPatternStart) > -1){
            whenFlag+=1
        }

        if (s.indexOf(whenPatternEnd) > -1){
            whenFlag-=1
        }
    }
}

