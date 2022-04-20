package wall

import wall.messages.MessageServiceImpl

fun main() {
    val valera = User(1, "Valera")
    val kirkorov = User(2, "Филипп Киркоров")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST1")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST2")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST3")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST4")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST5")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST6")
    MessageServiceImpl.sendMessage(valera.id, kirkorov.id, "TEST7")
    val test = MessageServiceImpl.read(5, kirkorov.id, valera.id)
    println(test)
}