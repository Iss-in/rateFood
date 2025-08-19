
'use client';

import { useEffect, useState } from 'react';
import { Dish } from '@/app/components/DishCard';
import { Restaurant } from '@/app/components/RestaurantCard';
import { DishCard } from '@/app/components/DishCard';
import { RestaurantCard } from '@/app/components/RestaurantCard';
import { fetchWithAuth } from '@/lib/api';

export function SubmittedRequestsPage() {
  const [submittedDishes, setSubmittedDishes] = useState<Dish[]>([]);
  const [submittedRestaurants, setSubmittedRestaurants] = useState<Restaurant[]>([]);

  useEffect(() => {
    // Fetch submitted dishes
    fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/foodapp/user/submissions/dishes`)
      .then(res => res.json())
      .then(data => setSubmittedDishes(data.data))
      .catch(err => console.error('Failed to fetch submitted dishes:', err));

    // Fetch submitted restaurants
    fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/foodapp/user/submissions/restaurants`)
      .then(res => res.json())
      .then(data => setSubmittedRestaurants(data.data))
      .catch(err => console.error('Failed to fetch submitted restaurants:', err));
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-3xl font-bold mb-8">Your Submitted Requests</h1>
      
      <section>
        <h2 className="text-2xl font-semibold mb-4">Submitted Dishes</h2>
        {submittedDishes.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {submittedDishes.map(dish => (
              <DishCard key={dish.id} dish={dish} onRemove={() => {}}  />
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You haven't submitted any dish requests yet.</p>
        )}
      </section>

      <section className="mt-12">
        <h2 className="text-2xl font-semibold mb-4">Submitted Restaurants</h2>
        {submittedRestaurants.length > 0 ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
            {submittedRestaurants.map(restaurant => (
              <RestaurantCard key={restaurant.id} restaurant={restaurant} onRatingChange={() => {}}   onRemove={() => {}}  />
            ))}
          </div>
        ) : (
          <p className="text-muted-foreground">You haven't submitted any restaurant requests yet.</p>
        )}
      </section>
    </div>
  );
}
