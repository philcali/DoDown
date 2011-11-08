package dodown

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey._

import java.util.logging.Level

import dispatch._

import java.io.File

object DoD {
  val http = new Http

  val base = :/("dwellingofduels.net") / "dodarchive"
}

case class DoD(prefix: String) {
  val Contest = """href="%s(.+)/">""".format(prefix).r
    
  val MP3 = """href="(.+)\.mp3">""".r

  val Rank = """(\d{2}).+""".r

  def preview() {
    import DoD.http

    Contest.findFirstIn(http(DoD.base as_str)).map { c =>
      val Contest(name) = c
      
      val locationName = "%s%s" format (prefix, name)

      println("The Contest is: %s" format(name))
      http(DoD.base / locationName as_str).split("\n").foreach { html =>
        MP3.findFirstIn(html).map { li =>
          val MP3(song) = li.replaceAll("%20", " ")

          println(song)
        }
      }
    }
  }

  def pullDown(base: String, threshold: Option[Int]) { 
    import DoD.http

    Contest.findFirstIn(http(DoD.base as_str)).map { c =>
      val Contest(name) = c

      val locationName = "%s%s" format (prefix, name)

      val location = new File(base, locationName)
      if (!location.exists) location.mkdir

      http(DoD.base / locationName as_str).split("\n").foreach { html =>
        MP3.findFirstIn(html).map { li =>
          val MP3(song) = li.replaceAll("&amp;", "&");
          val Rank(n) = song.replace("ZZ", "99")

          if (threshold.isEmpty || threshold.map(_ >= n.toInt).getOrElse(false)) {
            println("Retrieving %s..." format song)

            val mp3 = song + ".mp3"
            val out = new java.io.FileOutputStream(new File(location, mp3)) 

            http(DoD.base / locationName / mp3 >>> out)
          }
        }
      }
    }
  }

  def reTag(base: String) {
    val d = new java.io.File(base)

    val pulled = d.listFiles.find(_.getName.startsWith(prefix))

    pulled.foreach { dir =>
      val mp3Files = dir.listFiles.filter(_.isFile)

      for (file <- mp3Files) {
        val f = AudioFileIO read file

        val tag = f.getTag

        tag.setField(ARTIST, "DoD")
        tag.setField(ALBUM, dir.getName)

        f.commit()
      }
    }
  }
}

class DoDown extends xsbti.AppMain {
  case class Exit(code: Int) extends xsbti.Exit

  def run(config: xsbti.AppConfiguration) = {
    DoDown.main(config.arguments)
    Exit(0)
  }
}

object DoDown extends App {
  val Date = """\d{2}\-\d{2}""".r

  def printHelp() {
    println(
    """
      dodown [-r|-d|-p] [-t n] yy-mm [.]
        -p preview only
        -r retags only
        -d pulls only
        -t Rank threshold
        ie: 
          dodown 11-09 ~/Music/
          dodown -p 11-09
          dodown -d 11-09 ~/Music/
          dodown -r 11-09 ~/Music/
          dodown -d -t 5 11-09 ~/Music/
    """
    )
  }

  val pull = args.exists(_ == "-d")
  val preview = args.exists(_ == "-p")
  val retag = args.exists(_ == "-r")
  val threshold = args.exists(_ == "-t")

  val both = (pull && retag) || (!pull && !retag)

  def valid(date: String, base: String = ".") = 
    !Date.findFirstIn(date).isEmpty && new File(base).exists

  def run(date: String, thres: Option[Int] = None, base: String = ".") {
    val session = DoD(date)

    if (pull || both) {
      session pullDown (base, thres)
    }

    if (retag || both) {
      session reTag base
    }
  }

  args.filter(!_.startsWith("-")) match {
    case Array(t, date, base) if valid(date, base) =>
      run(date, Some(t.toInt), base) 
    case Array(date, base) if valid(date, base) =>
      run(date, None, base)
    case Array(t, date) if valid(date) =>
      run(date, Some(t.toInt))
    case Array(date) if valid(date) && preview => 
      DoD(date) preview
    case Array(date) if valid(date) =>
      run(date)
    case _ =>
      printHelp
  }
}
