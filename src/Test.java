import com.kingmang.voltage.VoltageVm;

public class Test {
    public static void main(String[] args) {
        VoltageVm m = new VoltageVm(false);
        
        //simple example
        m.load(new int[]{VoltageVm.CONST, 5, VoltageVm.CONST, 2, VoltageVm.ADD, VoltageVm.PRINT, VoltageVm.HALT});
        m.run();



    }
}
