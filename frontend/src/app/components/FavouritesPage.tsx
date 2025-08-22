'use client';
import { useEffect } from 'react';
import { DishCard } from '@/app/components/DishCard';
import { RestaurantCard } from '@/app/components/RestaurantCard';
import { useAppContext } from '@/app/contexts/AppContext';

export function FavouritesPage() {
  const {
    favouriteDishes,
    favouriteRestaurants,
    fetchFavourites,
    removeFavouriteDish,
    removeFavouriteRestaurant,
    loadingFavourites,
    selectedCity
  } = useAppContext();

  // Fetch favourites when component mounts or city changes
  useEffect(() => {
    if (selectedCity) {
      fetchFavourites();
    }
  }, [selectedCity , fetchFavourites ]);

  // Handle restaurant rating change (placeholder for now)
  const handleRestaurantRating = (restaurantId: string, rating: number) => {
    // You can implement this to update the rating in your backend
    console.log(`Restaurant ${restaurantId} rated ${rating}`);
  };

  if (loadingFavourites) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <p className="text-muted-foreground">Loading your favourites...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold mb-8">Your Favourites</h1>
      
      <section>
        <h2 className="text-2xl font-semibold mb-4">Favourite Dishes</h2>
        {favouriteDishes && favouriteDishes.length > 0 ? (
          <div
            className="flex space-x-4 overflow-x-auto scrollbar-hide pb-4"
            style={{ WebkitOverflowScrolling: 'touch' }}
          >
            {favouriteDishes.map((dish) => (
              <div key={dish.id} className="flex-shrink-0" style={{ width: 200 /* desired card width */ }}>
                <DishCard
                  dish={dish}
                  onFavouriteRemove={() => removeFavouriteDish(dish.id)}
                  onRemove={() => {}}
                  showMenu={false}
                  selectedCity=""
                />
              </div>
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You have not added any favourite dishes yet.</p>
        )}
      </section>

      <section className="mt-12">
        <h2 className="text-2xl font-semibold mb-4">Favourite Restaurants</h2>
        {favouriteRestaurants && favouriteRestaurants.length > 0 ? (
          favouriteRestaurants.length > 5 ? (
            <div
              className="flex space-x-4 overflow-x-auto scrollbar-hide pb-4"
              style={{ WebkitOverflowScrolling: 'touch' }}
            >
              {favouriteRestaurants.map((restaurant) => (
                <div
                  key={restaurant.id}
                  className="flex-shrink-0"
                  style={{ width: 200 /* or desired card width */ }}
                >
                  <RestaurantCard
                    restaurant={restaurant}
                    onRemove={() => {}}
                    onFavouriteRemove={() => removeFavouriteRestaurant(restaurant.id)}
                  />
                </div>
              ))}
            </div>
          ) : (
            <div className="grid grid-cols-3 sm:grid-cols-4 md:grid-cols-5 gap-6">
              {favouriteRestaurants.map((restaurant) => (
                <RestaurantCard
                  key={restaurant.id}
                  restaurant={restaurant}
                  onRemove={() => {}}
                  onFavouriteRemove={() => removeFavouriteRestaurant(restaurant.id)}
                />
              ))}
            </div>
          )
        ) : (
          <p className="text-muted-foreground">
            You have not added any favourite restaurants yet.
          </p>
        )}
      </section>

    </div>
  );
}