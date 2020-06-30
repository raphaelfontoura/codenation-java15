package br.com.codenation.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.codenation.model.OrderItem;
import br.com.codenation.model.Product;
import br.com.codenation.repository.ProductRepository;
import br.com.codenation.repository.ProductRepositoryImpl;

public class OrderServiceImpl implements OrderService {

	private ProductRepository productRepository = new ProductRepositoryImpl();

	/**
	 * Calculate the sum of all OrderItems
	 */
	@Override
	public Double calculateOrderValue(List<OrderItem> items) {
		Double value = 0.0;
		
		for (OrderItem orderItem : items) {
			Long id = orderItem.getProductId();
			Product product = productRepository.findById(id).get();
			if (product.getIsSale()) {
				value += product.getValue() * orderItem.getQuantity() * 0.8;
			} else {
				value += product.getValue() * orderItem.getQuantity();
			}
		}
		return value;
	}

	/**
	 * Map from idProduct List to Product Set
	 */
	@Override
	public Set<Product> findProductsById(List<Long> ids) {
		
		Set<Product> products = new HashSet<Product>();
		
		for (Long id : ids) {
			products.add(this.productRepository.findById(id).get());
		}
		return products;
	}

	/**
	 * Calculate the sum of all Orders(List<OrderIten>)
	 */
	@Override
	public Double calculateMultipleOrders(List<List<OrderItem>> orders) {
		Double value = 0.0;
		for (List<OrderItem> list : orders) {
			value += calculateOrderValue(list);
		}
		return value;
	}

	/**
	 * Group products using isSale attribute as the map key
	 */
	@Override
	public Map<Boolean, List<Product>> groupProductsBySale(List<Long> productIds) {
		Set<Product> setProducts = findProductsById(productIds);
		List<Product> productsIsSale = setProducts.stream()
				.filter(p -> p.getIsSale().equals(true)).collect(Collectors.toList());
		List<Product> productsIsNotSale = setProducts.stream()
				.filter(p -> p.getIsSale().equals(false)).collect(Collectors.toList());
		Map<Boolean, List<Product>> collectProducts = new HashMap<Boolean, List<Product>>();

		collectProducts.put(true, productsIsSale);
		collectProducts.put(false, productsIsNotSale);
		
		return collectProducts;
	}

}