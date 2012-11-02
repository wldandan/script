
process()

def process() {
    args.each{
        newFileName=it.replace("_high", "")
        File file = new File(newFileName)
        if (file.exists()){
            println "backup $newFileName"
            file.renameTo(new File(newFileName + ".bak"))
        }
        "git mv $it $newFileName".execute().text
    }
}






