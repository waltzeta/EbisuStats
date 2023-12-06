package com.ebisustats.BBDD;

import java.util.List;

public class DataStatusManager {
    //interfaces para usuarios
    public interface ReadUserStatus {
        void onUsersLoaded(List<User> listaUsuarios);
        void onUsersLoadFailed(String errorMessage);
    }

    public interface WriteUserStatus {
        void onUsersWriteSuccess();
        void onUsersWriteFailure(String errorMessage);
    }

    //interfaces para productos
    public interface ReadProductStatus {
        void onProductsLoaded(List<Product> listaProductos);
        void onProductsLoadFailed(String errorMessage);
    }

    public interface WriteProductStatus {
        void onProductsWriteSuccess();
        void onProductsWriteFailure(String errorMessage);
    }

    public interface GetProductByNameStatus {
        void onProductGet(Product product);

        void onProductGetFailed(String errorMessage);
    }

    //Interfaces para ventas
    public interface ReadSaleStatus {
        void onSalesLoaded(List<Sale> sales);
        void onSalesLoadFailed(String errorMessage);
    }

    public interface WriteSaleStatus {
        void onSalesWriteSuccess();
        void onSalesWriteFailure(String errorMessage);
    }
}
