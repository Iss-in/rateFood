
'use client';

import { useEffect, useState } from 'react';
import { Dish } from '@/app/components/DishCard';
import { Restaurant } from '@/app/components/RestaurantCard';
import { DishCard } from '@/app/components/DishCard';
import { RestaurantCard } from '@/app/components/RestaurantCard';
import { fetchWithAuth } from '@/lib/api';

export function FavouritesPage() {
  const [favouriteDishes, setFavouriteDishes] = useState<Dish[]>([]);
  const [favouriteRestaurants, setFavouriteRestaurants] = useState<Restaurant[]>([]);

  useEffect(() => {
    // Fetch favourite dishes
    fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/foodapp/dish/favourites/kanpur`)
      .then(res => res.json())
      .then(data => setFavouriteDishes(data.data))
      .catch(err => console.error('Failed to fetch favourite dishes:', err));

    // Fetch favourite restaurants
    fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/foodapp/restaurant/favourites/kanpur`)
      .then(res => res.json())
      .then(data => setFavouriteRestaurants(data.data))
      .catch(err => console.error('Failed to fetch favourite restaurants:', err));
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold mb-8">Your Favourites</h1>
      
      <section>
        <h2 className="text-2xl font-semibold mb-4">Favourite Dishes</h2>
        {favouriteDishes.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {favouriteDishes.map(dish => (
              <DishCard
                  key={dish.id}
                  dish={dish}
                onRemove={() => setFavouriteDishes(prev => prev.filter(d => d.id !== dish.id))}
              />
            ))}
          </div>
        ) : (
            <p className="text-muted-foreground">You have not added any favourite dishes yet.</p>        )}
      </section>

      <section className="mt-12">
        <h2 className="text-2xl font-semibold mb-4">Favourite Restaurants</h2>
        {favouriteRestaurants.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {favouriteRestaurants.map(restaurant => (
              <RestaurantCard
                  key={restaurant.id}
                  restaurant={restaurant}
                  onRatingChange={() => {}}
                  onRemove={() => setFavouriteDishes(prev => prev.filter(d => d.id !== restaurant.id))}
              />
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You have not added any favourite restaurants yet.</p>
        )}
      </section>
    </div>
  );
}
