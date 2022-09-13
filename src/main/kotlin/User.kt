import java.time.LocalDateTime
import java.time.Duration

abstract class User(private var name: String, private var mailID: String, private var age: Int) {
    abstract val maxBooks: Int
    abstract val userType: UserTypes
    private var userAccount: UserAccount? = null
    private val data: UserInterface = LibraryData
    private var inLibrary: Boolean = false

    init {
        this.userAccount = UserAccount(this.mailID)
        Authenticator.addCredential(mailID)
    }

    fun borrowBook(book: Book): String {
        val now: LocalDateTime = LocalDateTime.now()
        if (this.userType == UserTypes.STUDENT && userAccount?.booksTaken?.count()!! > (this as Student).maxBooks) {
            return "Return some books to borrow books"
        } else if (this.userType == UserTypes.FACULTY && userAccount?.booksTaken?.count()!! > (this as Faculty).maxBooks) {
            return "Return some books to borrow books"
        }
        userAccount?.booksTaken?.put(book, now)
        data.borrowBook(book, mailID)
        return "Book borrowed"
    }

    fun returnBook(bookID: Int) {
        var book: Book? = null
        for(eachBook in userAccount?.booksTaken?.keys!!) {
            if (eachBook.bookID == bookID) {
                book = eachBook
            }
        }
        val now: LocalDateTime = LocalDateTime.now()
        val duration = Duration.between(now, userAccount?.booksTaken?.get(book))
        Librarian.addFineToUser(duration.toDays().toInt(), mailID)
        if (book != null) {
            userAccount?.booksTaken?.remove(book)
            userAccount?.booksReturned?.add(book)
            data.returnBook(book)
        }
    }

    fun requestBook(title: String, reason: String) {
        data.requestBook(title, reason)
    }

    fun booksInHand(): MutableMap<Book, LocalDateTime>? {
        return userAccount?.booksTaken
    }

    fun enterLibrary() {
        inLibrary = true
    }

    fun exitLibrary() {
        inLibrary = false
    }

    fun payFine(amount: Int) {
        Librarian.removeFineFromUser(amount, mailID)
    }
}