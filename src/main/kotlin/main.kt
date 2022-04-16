package wall

import wall.wall.NoteService

fun main() {
    val valera = User(1, "Valera")
    val kirkorov = User(2, "Филипп Киркоров")
    NoteService.addNote("ewq0", "241jkl124", kirkorov.id)
    NoteService.addNote("ew4", "241jkl132124", kirkorov.id)
    NoteService.addNote("ewq3", "241jkl131224", kirkorov.id)
    NoteService.addNote("ewq2", "241jkl123124", kirkorov.id)
    NoteService.addNote("ewq1", "12241jkl124", kirkorov.id)
    println(NoteService.getNotes(kirkorov.id, false, 0, 20, 5, 1, 10))
    println(NoteService.getNotes(kirkorov.id, false, 0, 20, 2))
    NoteService.createComment(2, valera.id, kirkorov.id, "test1")
    NoteService.createComment(2, valera.id, kirkorov.id, "test2")
    NoteService.createComment(2, valera.id, kirkorov.id, "test3")
    NoteService.createComment(2, valera.id, kirkorov.id, "test4")
    NoteService.createComment(2, valera.id, kirkorov.id, "test5")
    println(NoteService.getComments(2, kirkorov.id, true))
}