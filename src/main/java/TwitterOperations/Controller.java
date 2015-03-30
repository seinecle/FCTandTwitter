/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TwitterOperations;

import java.util.List;

/**
 *
 * @author C. Levallois
 */
public class Controller {

    public static void main(String[] args) {
        Thread searchWorker = new SearchAPI();
        searchWorker.start();

    }
}
