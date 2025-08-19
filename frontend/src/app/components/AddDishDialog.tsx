import React from "react";
import { useState } from "react";
import { useEffect } from "react";
import { useRef } from "react";
import { fetchWithAuth } from "@/lib/api";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Textarea } from "./ui/textarea";
import { Label } from "./ui/label";
import { Badge } from "./ui/badge";
import { Plus } from "lucide-react";
import { Dish } from "./DishCard";
import toast from 'react-hot-toast';
interface AddDishDialogProps {
  onAddDish: (dish: Omit<Dish, "id" | "rating">) => void;
  selectedCity: string;
  open?: boolean; // add this
  onOpenChange?: (open: boolean) => void; // add
}

interface RestaurantNameDropdownProps {
  selectedCity: string;
  restaurant: string;
  setRestaurant: (name: string) => void;
  onValidate: (isValid: boolean) => void; // notify parent validation status
}


// Move component OUTSIDE of AddDishDialog
const RestaurantNameDropdown = React.memo(function RestaurantNameDropdown({
                                                                            selectedCity,
                                                                            restaurant,
                                                                            setRestaurant,
                                                                            onValidate,
                                                                          }: RestaurantNameDropdownProps) {
  const [query, setQuery] = useState(restaurant);
  const prevRestaurantRef = useRef(restaurant);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1); // -1 means nothing highlighted

  const debounceRef = useRef<NodeJS.Timeout | null>(null);

  const [restaurantError, setRestaurantError] = useState("");

  const handleBlur = () => {
    onValidate(false)

    setTimeout(() => {
      setShowDropdown(false);
      if (query.trim() === "") {
        setRestaurantError("");
        onValidate(true); // valid if empty (if required, handle separately)
        return;
      }
      const exactMatch = suggestions.some(
          (name) => name.toLowerCase() === query.trim().toLowerCase()
      );
      console.log(query, suggestions,exactMatch)

      if (!exactMatch) {
        setRestaurantError("Please select a restaurant from the list.");
      } else {
        setRestaurantError("");
        onValidate(true);   // <-- Add this line
        // console.log(`resutaurent validity ${isRestaurantValid}`)
      }
    }, 200);
  };


  useEffect(() => {
    if (restaurant !== prevRestaurantRef.current) {
      setQuery(restaurant);
      prevRestaurantRef.current = restaurant;
    }
  }, [restaurant]);

  // Set highlightedIndex to 0 if suggestions change and not empty
  useEffect(() => {
    if (showDropdown && suggestions.length > 0) {
      setHighlightedIndex(0);
    } else {
      setHighlightedIndex(-1);
    }
  }, [suggestions, showDropdown]);

  useEffect(() => {
    if (!selectedCity || !query) {
      setSuggestions([]);
      return;
    }
    if (debounceRef.current) clearTimeout(debounceRef.current);
    debounceRef.current = setTimeout(() => {
      fetchWithAuth(
          `${process.env.NEXT_PUBLIC_API_URL}/foodapp/restaurant/${selectedCity}?name=${encodeURIComponent(
              query
          )}&page=0&size=10`
      )
          .then((res) => (res.ok ? res.json() : Promise.reject("Failed to fetch")))
          .then((data) => {
            if (data && Array.isArray(data.data)) {
              const names = data.data.map((restaurant: { name: string }) => restaurant.name);
              setSuggestions(names);
            } else {
              setSuggestions([]);
            }
          })
          .catch(() => setSuggestions([]));
    }, 300);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [selectedCity, query]);

  // Keyboard navigation handler
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (!showDropdown || suggestions.length === 0) return;

    if (e.key === "ArrowDown") {
      e.preventDefault();
      setHighlightedIndex((prev) => (prev + 1) % suggestions.length);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setHighlightedIndex((prev) => (prev - 1 + suggestions.length) % suggestions.length);
    } else if (e.key === "Enter") {
      e.preventDefault();
      if (highlightedIndex >= 0 && highlightedIndex < suggestions.length) {
        handleSelect(suggestions[highlightedIndex]);
      }
    } else if (e.key === "Escape") {
      setShowDropdown(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = e.target.value;
    setQuery(val);
    setRestaurant(val);
    setShowDropdown(true);
    setRestaurantError(""); // Clear error on input change
    onValidate(false); // assume invalid until valid
  };

  const handleSelect = (restaurantName: string) => {
    setQuery(restaurantName);
    setRestaurant(restaurantName);
    setShowDropdown(false);
    setRestaurantError(""); // Clear error on valid select
    onValidate(true);
  };



  return (
      <div className="relative">
        <Label htmlFor="restaurant-name">Restaurant Name *</Label>
        <Input
            id="restaurant-name"
            value={query}
            onChange={handleInputChange}
            onKeyDown={handleKeyDown}
            required
            autoComplete="off"
            onFocus={() => setShowDropdown(true)}
            onBlur={handleBlur}
            aria-invalid={restaurantError ? "true" : "false"}
            aria-describedby={restaurantError ? "restaurant-error" : undefined}
        />
        {restaurantError && (
            <p id="restaurant-error" className="mt-1 text-red-600 text-sm">
              {restaurantError}
            </p>
        )}
        {showDropdown && (
            <ul className="absolute w-full bg-white border rounded shadow max-h-48 overflow-auto z-10">
              {query === "" ? (
                  <li className="px-3 py-2 text-gray-500 select-none">
                    Start typing to search restaurants...
                  </li>
              ) : suggestions.length > 0 ? (
                  suggestions.map((name, index) => (
                      <li
                          key={name}
                          className={`px-3 py-2 cursor-pointer hover:bg-gray-100 ${
                              index === highlightedIndex ? "bg-blue-100" : ""
                          }`}
                          onMouseDown={() => handleSelect(name)} // mouseDown to prevent input blur before click
                          onMouseEnter={() => setHighlightedIndex(index)} // highlight on hover
                      >
                        {name}
                      </li>
                  ))
              ) : (
                  <li className="px-3 py-2 text-gray-500 select-none">No restaurants found.</li>
              )}
            </ul>
        )}
      </div>
  );
});


export function AddDishDialog({ onAddDish, open, onOpenChange , selectedCity }: AddDishDialogProps) {

  const setIsOpen = onOpenChange ?? (() => {});

  const [isRestaurantValid, setIsRestaurantValid] = useState(false);  // <-- ADD THIS
  // const [open, setOpen] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    restaurant: "",
    description: "",
    tags: [] as string[],
    image: ""
  });
  const [currentTag, setCurrentTag] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (!isRestaurantValid) {
      // Optionally, show a general error or focus input
      console.log(isRestaurantValid)
      toast.error("Please select a valid restaurant.");
      return;
    }
    if (formData.name && formData.restaurant) {
      onAddDish({
        name: formData.name,
        restaurant: formData.restaurant,
        description: formData.description,
        tags: formData.tags,
        image: formData.image || `https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400&h=300&fit=crop`,
      });
      setFormData({
        name: "",
        restaurant: "",
        description: "",
        tags: [],
        image: ""
      });
      setIsOpen(false);
    }
  };

  const addTag = () => {
    if (currentTag && !formData.tags.includes(currentTag)) {
      setFormData(prev => ({ ...prev, tags: [...prev.tags, currentTag] }));
      setCurrentTag("");
    }
  };

  const removeTag = (tag: string) => {
    setFormData(prev => ({ ...prev, tags: prev.tags.filter(t => t !== tag) }));
  };

  return (
      <Dialog open={open} onOpenChange={setIsOpen}>
        <DialogTrigger asChild>
          <button
              data-slot="button"
              className="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive hover:bg-primary/90 h-9 px-4 py-2 bg-gradient-to-r from-orange-500 to-red-500 hover:from-orange-600 hover:to-red-600 text-white shadow-lg"
          >
            <Plus className="h-4 w-4 mr-2" />
            Add Dish
          </button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Add New Dish</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Label htmlFor="dish-name">Dish Name *</Label>
              <Input
                  id="dish-name"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  required
              />
            </div>

            <div>
              <RestaurantNameDropdown
                  selectedCity={selectedCity}
                  restaurant={formData.restaurant}
                  setRestaurant={(restaurant: string) => setFormData(prev => ({ ...prev, restaurant }))}
                  onValidate={setIsRestaurantValid}
              />
            </div>

            <div>
              <Label htmlFor="description">Description</Label>
              <Textarea
                  id="description"
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
              />
            </div>

            <div>
              <Label>Tags</Label>
              <div className="flex space-x-2 mb-2">
                <Input
                    placeholder="Add tag"
                    value={currentTag}
                    onChange={(e) => setCurrentTag(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), addTag())}
                />
                <Button type="button" onClick={addTag} size="sm">Add</Button>
              </div>
              <div className="flex flex-wrap gap-1">
                {formData.tags.map((tag) => (
                    <Badge key={tag} className="gap-1">
                      {tag}
                      <button
                          type="button"
                          className="p-0 m-0 text-sm leading-none cursor-pointer"
                          onClick={() => removeTag(tag)}
                      >
                        ×
                      </button>
                    </Badge>
                ))}
              </div>
            </div>

            <div>
              <Label htmlFor="image">Image URL</Label>
              <Input
                  id="image"
                  value={formData.image}
                  onChange={(e) => setFormData(prev => ({ ...prev, image: e.target.value }))}
                  placeholder="Optional - will use default if empty"
              />
            </div>

            <div className="flex justify-end space-x-2">
              <Button type="button" onClick={() => setIsOpen(false)}>
                Cancel
              </Button>
              <Button type="submit">Add Dish</Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
  );
}