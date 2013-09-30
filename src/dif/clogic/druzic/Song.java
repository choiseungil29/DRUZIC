package dif.clogic.druzic;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 9. 25.
 * Time: 오전 5:30
 * To change this template use File | Settings | File Templates.
 */
public class Song {
    public int Id;
    public String Name;
    public String MelodySequence;

    public Song(int id, String name, String melodySequence) {
        Id = id;
        Name = name;
        MelodySequence = melodySequence;
    }
}
