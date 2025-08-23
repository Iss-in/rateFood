'use client';
import { useEffect } from 'react';
import { DishCard } from '@/app/components/DishCard';
import { RestaurantCard } from '@/app/components/RestaurantCard';
import { useSafeRouter } from '../hooks/useSafeRouter';
import { useAppContext } from '@/app/contexts/AppContext';

export function SubmittedRequestsPage() {
  const {
    submittedDishes,
    submittedRestaurants,
    fetchSubmittedRequests,
    loadingSubmittedRequests
  } = useAppContext();

  const { isSubmittedPage, isReady } = useSafeRouter();

  useEffect(() => {
    fetchSubmittedRequests();
  }, [fetchSubmittedRequests]);

  if (loadingSubmittedRequests) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <h1 className="text-3xl font-bold mb-8">Your Pending Submitted Requests</h1>
        <p className="text-muted-foreground">Loading your submitted requests...</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold mb-8">Your Pending Submitted Requests</h1>

      {/* <section>
        <h2 className="text-2xl font-semibold mb-4">Submitted Dishes</h2>
        {submittedDishes && submittedDishes.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {submittedDishes.map(dish => (
              <DishCard 
                key={dish.id} 
                dish={dish} 
                onRemove={() => {}} 
                isSubmittedPage={isSubmittedPage} 
              />
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You have not submitted any dish requests yet.</p>
        )}
      </section> */}

      <section>
        <h2 className="text-2xl font-semibold mb-4">Submitted Dishes</h2>
        {submittedDishes && submittedDishes.length > 0 ? (
          <div
            className="flex space-x-4 overflow-x-auto scrollbar-hide pb-4"
            style={{ WebkitOverflowScrolling: 'touch' }}
          >
            {submittedDishes.map((dish) => (
              <div key={dish.id} className="flex-shrink-0" style={{ width: 200 /* desired card width */ }}>
                <DishCard
                  key={dish.id}
                  dish={dish}
                  onRemove={() => { }}
                  isSubmittedPage={isSubmittedPage}
                  onFavouriteRemove={() => { }}
                  selectedCity=""
                  showMenu={false}
                />
              </div>
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You have not added any dishe request yet.</p>
        )}
      </section>

      <section className="mt-12">
        <h2 className="text-2xl font-semibold mb-4">Submitted Restaurants</h2>
        {submittedRestaurants && submittedRestaurants.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {submittedRestaurants.map(restaurant => (
              <RestaurantCard
                key={restaurant.id}
                restaurant={restaurant}
                onFavouriteRemove={() => { }}
                  isSubmittedPage={isSubmittedPage}
                // onRatingChange={(restaurant.id) => { }}
                onRemove={() => { }}
              />
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You have not submitted any restaurant requests yet.</p>
        )}
      </section>
    </div>
  );
}