package upgrade_saturn

import scala.util.{Using, Try, Success, Failure}

import java.io.{BufferedOutputStream, FileOutputStream, File}

def upgrade(currentVersion: String): Unit = try {
    val r = requests.get("https://github.com/phyalexander/Saturn/releases/latest")
    val tag = r.url.split("/").last
    val version = tag.drop(1) // drop because of 'v' in '.../releases/v0.2.0'

    if (version == currentVersion) {
        println("You already have the latest version of Saturn installed.")
        return
    }

    print(s"\nA new version $version was found.\nDo you want to install it? [y/n] ")
    scala.io.StdIn.readLine() match {
        case "y" | "yes" => println("Start installing...")
        case _ => {
            println("Installing cancelled...")
            return
        }
    }

    val home = utils.SATURN_HOME.getPath

    val load_link = s"https://codeload.github.com/phyalexander/Saturn/zip/refs/tags/$tag"
    val data: Array[Byte] = requests.get(load_link).contents
    val zipFile = s"$home/Saturn.zip"
    Using(new BufferedOutputStream(new FileOutputStream(zipFile)))(
        _.write(data, 0, data.length)).get

    val runtime = Runtime.getRuntime
    runtime.exec(s"unzip $zipFile -d $home").waitFor
    File(zipFile).delete()
    val projectDir = s"$home/Saturn-$version"
    val jarFile = projectDir ++ "/jars/Saturn.jar"
    val stdlibDir = projectDir ++ "/language/std"
    runtime.exec(s"cp -f $jarFile $home/Saturn.jar").waitFor()
    runtime.exec(s"cp -rf $stdlibDir $home/std").waitFor()
    runtime.exec(s"rm -rf $projectDir").waitFor()

    println(s"Successfully updated: $currentVersion -> $version!\nHave a nice day!")

} catch {
    case ex: requests.RequestsException => println(ex.message)
    case ex: java.io.IOException => println(ex.getMessage)
}
