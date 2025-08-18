'use client'
import {useState, useMemo, useEffect} from "react";
import { Navbar } from "./components/Navbar";
import { FilterPanel } from "./components/FilterPanel";
import { DishCard, Dish } from "./components/DishCard";
import { RestaurantCard, Restaurant } from "./components/RestaurantCard";
import { AddDishDialog } from "./components/AddDishDialog";
import { AddRestaurantDialog } from "./components/AddRestaurantDialog";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "./components/ui/tabs";
import { Toaster } from 'react-hot-toast';
import {Button} from "react-day-picker";
import {Plus} from "lucide-react";
// Mock data
const initialDishes: Dish[] = [
  {
    id: "1",
    name: "Truffle Pasta",
    restaurant: "Luigi's Italian",
    description: "Fresh handmade pasta with black truffle and parmesan cheese",
    // price: 28.99,
    // rating: 4,
    tags: ["Italian", "Pasta", "Luxury", "Vegetarian"],
    image: "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=400&h=300&fit=crop",
    // location: "Downtown",
    // distance: 2.3
  }
];




const initialRestaurants: Restaurant[] = [
  {
    id: "1",
    name: "Luigi's Italian",
    cuisine: "Italian",
    description: "Authentic Italian cuisine with fresh ingredients and traditional recipes",
    rating: 4,
    tags: ["Italian", "Fine Dining", "Romantic", "Wine Bar"],
    image: "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=400&h=300&fit=crop",
    // location: "Downtown",
    // distance: 2.3,
    // hours: "5 PM - 11 PM",
    // phone: "(555) 123-4567",
    // priceRange: "$$$"
  }
];

export default function App() {
  // const [selectedCity, setSelectedCity] = useState<string | null>(null);
  // const [selectedCity, setSelectedCity] = useState<string >(() => {
  //   if (typeof window !== 'undefined') {
  //     return localStorage.getItem('selectedCity') || '';
  //   }
  //   return '';
  // });

  const [hasMounted, setHasMounted] = useState(false);
  const [selectedCity, setSelectedCity] = useState("");



  const [selectedTab, setSelectedTab] = useState("dishes");
  const [dishes, setDishes] = useState<Dish[]>([]);
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);

  // const [currentPage, setCurrentPage] = useState(0);
  // const [totalPages, setTotalPages] = useState(1); // or totalCount if you prefer

  const [dishesCurrentPage, setDishesCurrentPage] = useState(0);
  const [dishesTotalPages, setDishesTotalPages] = useState(1);
  const [loadingDishes, setLoadingDishes] = useState(false);
  const [hasMoreDishes, setHasMoreDishes] = useState(true);

  const [restaurantsCurrentPage, setRestaurantsCurrentPage] = useState(0);
  const [restaurantsTotalPages, setRestaurantsTotalPages] = useState(1);
  const [loadingRestaurants, setLoadingRestaurants] = useState(false);
  const [hasMoreRestaurants, setHasMoreRestaurants] = useState(true);

  // Filter states
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
  }, []);

  useEffect(() => {
    setRestaurants([]);
    setRestaurantsCurrentPage(0);
    setHasMoreRestaurants(true);
  }, [selectedCity]);


  useEffect(() => {
    if (!selectedCity) return; // Exit early if city is not selected yet
    setLoadingRestaurants(true);
    // localStorage.setItem('selectedCity', selectedCity);

    fetch(`${process.env.NEXT_PUBLIC_API_URL}/restaurant/${selectedCity}?page=${restaurantsCurrentPage}`) // your backend endpoint URL here
        .then((res) => {
          if (!res.ok) throw new Error(`Failed to fetch restaurants for ${selectedCity}?page=${restaurantsCurrentPage}`);
          // console.log(res);
          return res.json();
        })
        .then(data => {
          setRestaurants(prev =>
              restaurantsCurrentPage === 0
                  ? data.data
                  : [...prev, ...data.data]
          );
          setRestaurantsTotalPages(data.totalPages);
          setHasMoreRestaurants(restaurantsCurrentPage + 1 < data.totalPages);
        })
        .catch(err => console.error(err))
        .finally(() => setLoadingRestaurants(false));
    // console.log("restaurents are ")
    // console.log(restaurants)
  }, [selectedCity, restaurantsCurrentPage]);

  useEffect(() => {
    function handleScroll() {
      if (
          window.innerHeight + window.scrollY >= document.body.offsetHeight - 300 // 300px from bottom
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
    if (!selectedCity) return; // Exit early if city is not selected yet
    setLoadingDishes(true);
    // localStorage.setItem('selectedCity', selectedCity);

    fetch(`${process.env.NEXT_PUBLIC_API_URL}/dish/${selectedCity}?page=${dishesCurrentPage}`) // your backend endpoint URL here
        .then((res) => {
          if (!res.ok) throw new Error(`Failed to fetch dishes for ${selectedCity}?page=${dishesCurrentPage}`);
          // console.log(res);
          return res.json();
        })
        .then(data => {
          setDishes(prev =>
              dishesCurrentPage === 0
                  ? data.data
                  : [...prev, ...data.data]
          );
          setDishesTotalPages(data.totalPages);
          setHasMoreDishes(dishesCurrentPage + 1 < data.totalPages);
        })
        .catch(err => console.error(err))
        .finally(() => setLoadingDishes(false));
    // console.log("restaurents are ")
    // console.log(restaurants)
  }, [selectedCity, dishesCurrentPage]);


  // Get available tags
  const dishTagOptions = useMemo(() => {
    const allTags = dishes.flatMap(dish => dish.tags);
    return [...new Set(allTags)];
  }, [dishes]);

  const restaurantTagOptions = useMemo(() => {
    const allTags = restaurants.flatMap(restaurant => restaurant.tags);
    return [...new Set(allTags)];
  }, [restaurants]);

  // Filter functions
  const filteredDishes = useMemo(() => {
    return dishes.filter(dish => {
      const matchesSearch = dish.name.toLowerCase().includes(dishSearch.toLowerCase()) ||
                           dish.restaurant.toLowerCase().includes(dishSearch.toLowerCase());
      const matchesTags = dishTags.length === 0 || dishTags.some(tag => dish.tags.includes(tag));
      // const withinRange = dish.distance <= dishRange;
      // return matchesSearch && matchesTags && withinRange;
      //
      return matchesSearch && matchesTags ;
    });
  }, [dishes, dishSearch, dishTags, dishRange]);

  const filteredRestaurants = useMemo(() => {
    return restaurants.filter(restaurant => {
      const matchesSearch = restaurant.name.toLowerCase().includes(restaurantSearch.toLowerCase()) ||
                           restaurant.cuisine.toLowerCase().includes(restaurantSearch.toLowerCase());
      const matchesTags = restaurantTags.length === 0 || restaurantTags.some(tag => restaurant.tags.includes(tag));
      // const withinRange = restaurant.distance <= restaurantRange;
      
      // return matchesSearch && matchesTags && withinRange;
      return matchesSearch && matchesTags ;

    });
  }, [restaurants, restaurantSearch, restaurantTags, restaurantRange]);

  // Handler functions
  const handleDishRating = (dishId: string, rating: number) => {
    setDishes(prev => prev.map(dish => 
      dish.id === dishId ? { ...dish, rating } : dish
    ));
  };

  const handleRestaurantRating = (restaurantId: string, rating: number) => {
    setRestaurants(prev => prev.map(restaurant =>
      restaurant.id === restaurantId ? { ...restaurant, rating } : restaurant
    ));
  };

  const handleAddDish = async (newDish: Omit<Dish, "id" | "rating">) => {
    const dish: Dish = {
      ...newDish,
      id: Date.now().toString(),
      // rating: 0
    };
    try {
      // Replace 'http://your-backend-api/dishes' with your actual backend URL
      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/dish`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newDish)
      });

      if (!response.ok) {
        throw new Error(`Server error: ${response.statusText}`);
      }

      // Assuming backend returns the created dish with id and rating assigned
      const createdDish: Dish = await response.json();

      // Add the returned dish (with id and rating) to state
      // setDishes(prev => [createdDish, ...prev]);
      // console.log(dishes)
    } catch (error) {
      console.error('Failed to add dish:', error);
      // Optionally, show user-friendly error message or fallback action
    }

    setDishes(prev => [dish, ...prev]);
  };

  const handleAddRestaurant = async (newRestaurant: Omit<Restaurant, "id" | "rating">) => {
    const restaurant: Restaurant = {
      ...newRestaurant,
      id: Date.now().toString(),
      rating: 0
    };

    const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/restaurant`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ ...newRestaurant, city: selectedCity })
    });

    setRestaurants(prev => [restaurant, ...prev]);
  }

  // useEffect(() => {
  //   if (selectedTab === "restaurants") {
  //     console.log("Restaurants tab selected, restaurants:", restaurants);
  //   }
  // }, [selectedTab, restaurants]);

  // load city on startup
  useEffect(() => {
    const savedCity = localStorage.getItem('selectedCity');
    if (savedCity) {
      setSelectedCity(savedCity);
    }
  }, []);


  if (!hasMounted) return null; // Or loading placeholder

  //
  function AddDishDialogFloatingTrigger({ onAddDish, selectedCity }: { onAddDish: (newDish: Omit<Dish, "id" | "rating">)  => void, selectedCity: string }) {
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
      <Navbar
          selectedCity={selectedCity}
          onCityChange={setSelectedCity}
          selectedTab={selectedTab}
          onTabChange={setSelectedTab}
          onAddDish={handleAddDish}
          onAddRestaurant={handleAddRestaurant}
      />
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 bg-background-secondary">
        <Tabs value={selectedTab} className="w-full   onValueChange={setSelectedTab}">
          <TabsContent value="dishes" className="space-y-6" hidden={selectedTab !== "dishes"}>
            <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
              <div className="lg:col-span-1">
                <FilterPanel
                  onSearchChange={setDishSearch}
                  onTagsChange={setDishTags}
                  onRangeChange={setDishRange}
                  selectedTags={dishTags}
                  availableTags={dishTagOptions}
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
                      onRatingChange={handleDishRating}
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
                  onTagsChange={setRestaurantTags}
                  onRangeChange={setRestaurantRange}
                  selectedTags={restaurantTags}
                  availableTags={restaurantTagOptions}
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
        {/*<div className="fixed bottom-4 right-4 flex flex-col space-y-4 sm:hidden z-50">*/}
          {selectedTab === "dishes" && selectedCity && (
              <AddDishDialogFloatingTrigger onAddDish={handleAddDish} selectedCity={selectedCity} />
          )}
          {selectedTab === "restaurants" && selectedCity && (
              <AddRestaurantDialogFloatingTrigger onAddRestaurant={handleAddRestaurant} />
          )}
        {/*</div>*/}
      </div>
    </div>

);
}