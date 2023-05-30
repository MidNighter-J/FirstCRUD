import com.novokren.exceptions.AlreadyUsedNameException;
import com.novokren.exceptions.RegionNotFoundException;
import com.novokren.model.Region;
import com.novokren.repository.RegionRepository;


public class Test {
    public static void main(String[] args) throws RegionNotFoundException, AlreadyUsedNameException {

        RegionRepository repository = new RegionRepository();

        repository.save(new Region("UA"));
        repository.save(new Region("USA"));
        repository.save(new Region("GB"));

        System.out.println(repository.getAll());

        System.out.println(repository.getById(1));

        repository.delete(repository.getById(1));

        System.out.println(repository.getAll());

        Region GBReg = repository.getById(3);
        GBReg.setName("PL");
        repository.update(GBReg);

        System.out.println(repository.getAll());


    }
}
