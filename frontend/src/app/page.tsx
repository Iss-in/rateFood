'use client'
import {useState, useMemo, useEffect} from "react";

import { FilterPanel } from "./components/FilterPanel";
import { DishCard, Dish } from "./components/DishCard";
import { RestaurantCard, Restaurant } from "./components/RestaurantCard";
import { AddDishDialog } from "./components/AddDishDialog";
import { AddRestaurantDialog } from "./components/AddRestaurantDialog";
import { Tabs, TabsContent } from "./components/ui/tabs";
import { fetchWithAuth } from "@/lib/api";
import {Plus} from "lucide-react";
import { useAppContext } from "./contexts/AppContext";

export default function App() {
  const {
    selectedCity,
    setSelectedCity,
    selectedTab,
    setDishes,
    setRestaurants,
    handleAddDish,
    handleAddRestaurant,
  } = useAppContext();

  const [hasMounted, setHasMounted] = useState(false);
  const [dishes, setLocalDishes] = useState<Dish[]>([]);
  const [restaurants, setLocalRestaurants] = useState<Restaurant[]>([]);

  const [dishesCurrentPage] = useState(0);
  const [restaurantsCurrentPage, setRestaurantsCurrentPage] = useState(0);
  const [loadingRestaurants, setLoadingRestaurants] = useState(false);
  const [hasMoreRestaurants, setHasMoreRestaurants] = useState(true);
  const [loadingDishes, setLoadingDishes] = useState(false);
  const [hasMoreDishes, setHasMoreDishes] = useState(false);

  const [dishSearch, setDishSearch] = useState("");
  const [dishTags, setDishTags] = useState<string[]>([]);
  const [dishRange, setDishRange] = useState(10);
  
  const [restaurantSearch, setRestaurantSearch] = useState("");
  const [restaurantTags, setRestaurantTags] = useState<string[]>([]);
  const [restaurantRange, setRestaurantRange] = useState(10);

  useEffect(() => {
    setHasMounted(true);
    const city = localStorage.getItem('selectedCity') || '';
    setSelectedCity(city);
  }, [setSelectedCity]);

  useEffect(() => {
    setLocalRestaurants([]);
    setRestaurantsCurrentPage(0);
    setHasMoreRestaurants(true);
  }, [selectedCity]);

  useEffect(() => {
    if (!selectedCity) return;
    setLoadingRestaurants(true);

    fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/foodapp/restaurant/${selectedCity}?page=${restaurantsCurrentPage}`)
        .then((res) => {
          if (!res.ok) throw new Error(`Failed to fetch restaurants for ${selectedCity}?page=${restaurantsCurrentPage}`);
          return res.json();
        })
        .then(data => {
          const newRestaurants = restaurantsCurrentPage === 0 ? data.data : [...restaurants, ...data.data];
          setLocalRestaurants(newRestaurants);
          setRestaurants(newRestaurants);
          setHasMoreRestaurants(restaurantsCurrentPage + 1 < data.totalPages);
        })
        .catch(err => console.error(err))
        .finally(() => setLoadingRestaurants(false));
  }, [selectedCity, restaurantsCurrentPage, setRestaurants]);

  useEffect(() => {
    function handleScroll() {
      if (
          window.innerHeight + window.scrollY >= document.body.offsetHeight - 300
          && !loadingRestaurants
          && hasMoreRestaurants
      ) {
        setRestaurantsCurrentPage(prev => prev + 1);
      }
    }
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [loadingRestaurants, hasMoreRestaurants]);

  useEffect(() => {
    if (!selectedCity) return;
    setLoadingDishes(true);

    fetchWithAuth(`${process.env.NEXT_PUBLIC_API_URL}/foodapp/dish/${selectedCity}?page=${dishesCurrentPage}`)
        .then((res) => {
          if (!res.ok) throw new Error(`Failed to fetch dishes for ${selectedCity}?page=${dishesCurrentPage}`);
          return res.json();
        })
        .then(data => {
          const newDishes = dishesCurrentPage === 0 ? data.data : [...dishes, ...data.data];
          setLocalDishes(newDishes);
          setDishes(newDishes);
          setHasMoreDishes(dishesCurrentPage + 1 < data.totalPages);
        })
        .catch(err => console.error(err))
        .finally(() => setLoadingDishes(false));
  }, [selectedCity, dishesCurrentPage, setDishes]);

  const filteredDishes = useMemo(() => {
    return dishes.filter(dish => {
      const matchesSearch = dish.name.toLowerCase().includes(dishSearch.toLowerCase()) ||
                           dish.restaurant.toLowerCase().includes(dishSearch.toLowerCase());
      const matchesTags = dishTags.length === 0 || dishTags.some(tag => dish.tags.includes(tag));
      return matchesSearch && matchesTags;
    });
  }, [dishes, dishSearch, dishTags]);

  const filteredRestaurants = useMemo(() => {
    return restaurants.filter(restaurant => {
      const matchesSearch = restaurant.name.toLowerCase().includes(restaurantSearch.toLowerCase()) ||
                           restaurant.cuisine.toLowerCase().includes(restaurantSearch.toLowerCase());
      const matchesTags = restaurantTags.length === 0 || restaurantTags.some(tag => restaurant.tags.includes(tag));
      return matchesSearch && matchesTags;
    });
  }, [restaurants, restaurantSearch, restaurantTags]);

  const handleRestaurantRating = (restaurantId: string, rating: number) => {
    const updatedRestaurants = restaurants.map(restaurant =>
      restaurant.id === restaurantId ? { ...restaurant, rating } : restaurant
    );
    setLocalRestaurants(updatedRestaurants);
    setRestaurants(updatedRestaurants);
  };

  if (!hasMounted) return null;

  function AddDishDialogFloatingTrigger({ onAddDish, selectedCity }: { onAddDish: (newDish: Omit<Dish, "id" | "rating" | "favoriteCount">)  => void, selectedCity: string }) {
    const [open, setOpen] = useState(false);
    return (
        <>
        {open && (
          <AddDishDialog open={open} onOpenChange={setOpen} onAddDish={onAddDish} selectedCity={selectedCity} />)}
          <div className="fixed bottom-4 right-4 z-50 block sm:hidden">
            <button
                onClick={() => setOpen(true)}
                className="w-14 h-14 rounded-full bg-gradient-to-br from-orange-500 to-red-500 text-white shadow-lg flex items-center justify-center"
                aria-label="Add Dish"
                title="Add Dish"
            >
              <Plus size={24} />
            </button>
          </div>
        </>
    );
  }

  function AddRestaurantDialogFloatingTrigger({ onAddRestaurant }: { onAddRestaurant: (newRestaurant: Omit<Restaurant, "id" | "rating">) => void }) {
    const [open, setOpen] = useState(false);
    return (
        <>
        {open && (
          <AddRestaurantDialog open={open} onOpenChange={setOpen} onAddRestaurant={onAddRestaurant}  />)}
          <div className="fixed bottom-4 right-4 z-50 block sm:hidden">
            <button
                onClick={() => setOpen(true)}
                className="w-14 h-14 rounded-full bg-gradient-to-br from-orange-500 to-red-500 text-white shadow-lg flex items-center justify-center"
                aria-label="Add Restaurant"
                title="Add Restaurant"
            >
              <Plus size={24} />
            </button>
          </div>
        </>
    );
  }

  return (
    <div className="min-h-screen bg-background-secondary">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 bg-background-secondary">
        <Tabs value={selectedTab} className="w-full">
          <TabsContent value="dishes" className="space-y-6" hidden={selectedTab !== "dishes"}>
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
              <div className="lg:col-span-1">
                <FilterPanel
                  onSearchChange={setDishSearch}
                  onRangeChange={setDishRange}
                  searchValue={dishSearch}
                  locationRange={dishRange}
                />
              </div>
              <div className="lg:col-span-3">
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
                  {filteredDishes.map(dish => (
                    <DishCard
                      key={dish.id}
                      dish={dish}
                      onRemove={() => void 0}
                    />
                  ))}
                </div>
                {filteredDishes.length === 0 && (
                  <div className="text-center py-12">
                    <p className="text-muted-foreground">No dishes found matching your criteria.</p>
                  </div>
                )}
              </div>
            </div>
          </TabsContent>

          <TabsContent value="restaurants" className="space-y-6" hidden={selectedTab !== "restaurants"}>
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
              <div className="lg:col-span-1">
                <FilterPanel
                  onSearchChange={setRestaurantSearch}
                  onRangeChange={setRestaurantRange}
                  searchValue={restaurantSearch}
                  locationRange={restaurantRange}
                />
              </div>
              <div className="lg:col-span-3">
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6">
                  {filteredRestaurants.map(restaurant => (
                    <RestaurantCard
                      key={restaurant.id}
                      restaurant={restaurant}
                      onRatingChange={handleRestaurantRating}
                      onRemove={() => void 0}
                    />
                  ))}
                </div>
                {filteredRestaurants.length === 0 && (
                  <div className="text-center py-12">
                    <p className="text-muted-foreground">No restaurants found matching your criteria.</p>
                  </div>
                )}
              </div>
            </div>
          </TabsContent>
        </Tabs>
        {selectedTab === "dishes" && selectedCity && (
            <AddDishDialogFloatingTrigger onAddDish={handleAddDish} selectedCity={selectedCity} />
        )}
        {selectedTab === "restaurants" && selectedCity && (
            <AddRestaurantDialogFloatingTrigger onAddRestaurant={handleAddRestaurant} />
        )}
      </div>
    </div>
  );
}