# DoD Downloader

This is a simple program to mass download DoD song entries. It also retags the
MP3's using [jaudiotagger][jaudiotagger].

## Installation

The easiest installation method is through [Nathan Hamblem][n8han]'s 
[conscript][conscript] Scala software installer.

```
> git clone git://github.com/philcali/DoDown.git
> mkdir DoDown
> sbt publish-local
> cs philcali/DoDown
```

## Usage

One app, two functions: Download the submitted entries and/or nice auto retagging
of the MP3 (specifically for use in Google Music).

One can limit the song by rank if so desired, with the `-t n` option. An example
would be:

```
dodown -t 3 11-09
```

Such a command would only pull songs ranked first through third place. I've copied
the program args from the source:

```
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
```

## TODO

- Ability to pull down an entire year with persistent settings.
- Refactor program argument portion.
- Perhaps make this in a library

[n8han]: https://github.com/n8han
[conscript]: https://github.com/n8han/conscript
[jaudiotagger]: http://www.jthink.net/jaudiotagger/
